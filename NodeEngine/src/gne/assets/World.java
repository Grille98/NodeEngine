package gne.assets;
import java.io.*;
//NodeList
public class World {
	
	private Node[] nodes;
	private WorldObject[][] decoLayers;
	private Player[] players;
	
	private boolean[] arrayFinalized;
	public World() {
		this(new Node[0]);
	}
	public World(Node[] nodes) {
		this.nodes = nodes;
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
	}
	public void addDecos(WorldObject[] addDeco,int layer) {
		addWorldObjects(decoLayers[layer],decoLayers[layer]=new WorldObject[decoLayers[layer].length+1],addDeco);
	}
	public void addNode(Node addNode) {
		addWorldObject(nodes,nodes = new Node[nodes.length+1],addNode);
	}
	public void addNodes(Node[] addNodes) {
		addWorldObjects(nodes,nodes = new Node[nodes.length+addNodes.length],addNodes);
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
	
	public void finalProcessing() {
		if (!arrayFinalized[0])deepSorting(decoLayers[0]);
		if (!arrayFinalized[1])deepSorting(decoLayers[1]);
		if (!arrayFinalized[2])deepSorting(decoLayers[2]);
		if (!arrayFinalized[3])deepSorting(nodes);
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
	
	public void Save(String path) {
		byte[] data = null;
		try {
			FileOutputStream fos = new FileOutputStream(path);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	public void Load(String path) {
		byte[] data = null;
		try {
			FileInputStream fis = new FileInputStream(path);
			DataInputStream dis = new DataInputStream(fis);
			
			dis.close();

		} catch (IOException e) {e.printStackTrace();}
	}
}