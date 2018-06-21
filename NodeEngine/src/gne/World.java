package gne;
import java.io.*;
import java.util.stream.Stream;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
//NodeList
public class World {
	
	int width,height;
	Node[] nodes;
	WorldObject[][] decoLayers;
	Player[] players;
	
	Node[][] groups;
	String[] groupNames;
	
	public boolean repeatX = false,repeatY = false;
	Node[] conectionsDrawList;
	
	Texture backgroundImage;
	
	private boolean[] arrayFinalized;
	public World() {
		this(0,0);
	}
	public World(int width,int height) {
		this(width,height,new Node[0]);
	}
	public World(int width,int height,Node[] nodes) {
		this.width = width;
		this.height = height;
		this.nodes = nodes;
		groups = new Node[0][];
		groupNames = new String[0];
		decoLayers = new WorldObject[3][0];
		arrayFinalized = new boolean[4];
	}
	
	//WTF?
	private void addWorldObject(WorldObject[] source,WorldObject[] traget,WorldObject addObject) {
		for (int i = 0;i<source.length;i++) traget[i] = source[i];
		traget[source.length] = addObject;
	}
	private void addWorldObjects(WorldObject[] source,WorldObject[] traget,WorldObject[] addObjects) {
		int index = 0;
		for (int i = 0;i<source.length;i++) traget[index++] = source[i];
		for (int i = 0;i<addObjects.length;i++) traget[index++] = addObjects[i];
	}
	
	public void addDeco(WorldObject addDeco,int layer) {
		addWorldObject(decoLayers[layer],decoLayers[layer]=new WorldObject[decoLayers[layer].length+1],addDeco);
		arrayFinalized[layer] = false;
	}
	public void addDecos(WorldObject[] addDeco,int layer) {
		addWorldObjects(decoLayers[layer],decoLayers[layer]=new WorldObject[decoLayers[layer].length+1],addDeco);
		arrayFinalized[layer] = false;
	}
	public void addNode(Node addNode) {
		addWorldObject(nodes,nodes = new Node[nodes.length+1],addNode);
		arrayFinalized[3] = false;
	}
	public void addNodes(Node[] addNodes, String groupName) {
		Node[][] newNodes = new Node[groups.length+1][];
		String[] newNames = new String[groupNames.length+1];
		for (int i = 0;i<groupNames.length;i++) {
			newNodes[i] = groups[i];
			newNames[i] = groupNames[i];
		}
		groups = newNodes;
		groupNames = newNames;
		groups[groups.length-1] = addNodes;
		groupNames[groupNames.length-1] = groupName;
		addNodes(addNodes);
	}
	public void addNodes(Node[] addNodes) {
		addWorldObjects(nodes,nodes = new Node[nodes.length+addNodes.length],addNodes);
		arrayFinalized[3] = false;
	}

	private <T extends WorldObject> void deepSorting(T[] cords) {
		if (cords == null||cords.length<2)return;
		int clutters;
		do {
			clutters = 0;
			for (int i = 0;i<cords.length-1;i++) {
				if (cords[i].posY>cords[i+1].posY) {
					T backup = cords[i];
					cords[i] = cords[i+1];
					cords[i+1] =  backup;
					clutters++;
				}
			}
		} while (clutters != 0);
	}
	
	void finalProcessing() {
		if (!arrayFinalized[0]) {
			deepSorting(decoLayers[0]);
			arrayFinalized[0] = true;
		}
		if (!arrayFinalized[1]) {
			deepSorting(decoLayers[1]);
			arrayFinalized[1] = true;
		}
		if (!arrayFinalized[2]) {
			deepSorting(decoLayers[2]);
			arrayFinalized[2] = true;
		}
		if (!arrayFinalized[3]) {
			deepSorting(nodes);
			for (int i = 0;i<nodes.length;i++) nodes[i].id = i;
			arrayFinalized[3] = true;
		}
	}
	
	public WorldObject[] getDeco(int layer) {
		return decoLayers[layer];
	}
	public Node getNode(int id) {
	if (id<0||id > nodes.length)return null;
		return nodes[id];
	}
	public Node getNode(String name) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].name == name) return nodes[i];
		}
		return null;
	}
	public Node[] getNodes() {
		return nodes;
	}
	public Node[] getNodeGroup(String name) {
		for (int i = 0;i<groupNames.length;i++) {
			if (name == groupNames[i]) return groups[i];
		}
		return null;
	}
	public void setBackgroundGraphic(Texture background) {
		
		
		backgroundImage = background;
		/*
		GraphicsContext gc = new Canvas(width,height).getGraphicsContext2D();
		
		Texture newBackground;
		PixelReader reader = background.getPixelReader();
		PixelWriter writer = gc.getPixelWriter();
		
		newBackground = gc.im;
		/*
		for (int ix = 0;ix<sw;ix++) {
			for (int iy = 0;iy<sh;iy++) {
				Color color = reader.getColor(sx+ix, sy+iy);
				if (color.isOpaque()) {
					writer.setColor(dx+ix, dy+iy, color);
				}
			}
		}
		*/
		
	}
	public void killPlayer(Player deadPlayer) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].owner == deadPlayer) nodes[i].owner = null;
		}
	}
	public void replacePlayer(Player oldPlayer,Player newPlayer) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].owner == oldPlayer) nodes[i].owner = newPlayer;
		}
	}
	public int getNumberOfNodesOwnedByPlayer(Player player) {
		int result = 0;
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].owner == player)result++;
		}
		return result;
	}
	public boolean isNodesContainsPlayer(Node[] nodes,Player player) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].getOwner() == player)return true;
		}
		return false;
	}
	public boolean isNodesOwndedByPlayer(Node[] nodes,Player player) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].getOwner() != player)return false;
		}
		return true;
	}
	
	public void save(String path) {
		byte[] data = null;
		try {
			PrintWriter  pw = new PrintWriter(path);
			pw.print(width+';');
			
			pw.flush();
			pw.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	public void saveMapScript(String path) {
		finalProcessing();
		try {
			PrintWriter  pw = new PrintWriter(path);
			pw.println("backGround:"+backgroundImage.path);
			pw.println("width:"+width);
			pw.println("height:"+width);
			pw.println("repeatX:"+repeatX);
			pw.println("repeatY:"+repeatY);
			
			pw.println("\nNodes{");
			for (int i = 0;i<nodes.length;i++) {
				pw.print("  ("+nodes[i].posX+","+nodes[i].posY+","+0+","+nodes[i].name+")");
				if (nodes[i].connections != null && nodes[i].connections.length > 0) {
					pw.print("->["+nodes[i].connections[0].id);
					for (int ic = 1;ic < nodes[i].connections.length;ic++) {
						pw.print(","+nodes[i].connections[ic].id);
					}
					pw.print("]");
				}
				pw.print("\n");
			}
			pw.println("}");

			pw.flush();
			pw.close();

		} catch (IOException e) {e.printStackTrace();}
	}
	public void loadMapScript(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("file:/../data/mp.txt"));
			int count = 1; br.lines().count();
			for (int i = 0;i<count;i++) {
				System.out.println(br.readLine());
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("tot!!!!!!!!!");
			e.printStackTrace();
		}
	}
	public void load(String path) {
		byte[] data = null;
		try {
			FileInputStream fis = new FileInputStream(path);
			DataInputStream dis = new DataInputStream(fis);
			
			dis.close();

		} catch (IOException e) {e.printStackTrace();}
	}
}
