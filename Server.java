import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is used to search for the file requested by the client
 * 
 * @author Swati Bhartiya (sxb4298)
 *
 */
public class Server extends UnicastRemoteObject implements CommonInterface {

	private static final long serialVersionUID = 1L;
	HashMap<String, File> fileMap = new HashMap<>();
	HashMap<Integer, String> servers = new HashMap<Integer, String>();
	HashMap<String, String> IPAddress = new HashMap<String, String>();
	HashMap<String, Integer> fileCount = new HashMap<String, Integer>();

	int leafLevel = 2; // need to change

	File fileReceived;
	File fileToBeSent;
	String fileToSend = "";

	String selfName;
	String clientName;
	String clientIPAddress;
	boolean checkLeaf = false;
	boolean checkIntermediate = false;
	boolean checkRoot = false;

	boolean fileFound = false;

	String rootOfFile = "";
	String fileNameToBeSent = "";

	CommonInterface clientObj;

	/**
	 * This is the constructor of the Server class
	 * 
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	protected Server() throws RemoteException, UnknownHostException {
		super();
		Registry reg = LocateRegistry.createRegistry(2020);
		reg.rebind("server", this);
		// System.out.println("Server object bound to registry");
		selfName = InetAddress.getLocalHost().getHostName();

		servers.put(0, "comet");
		servers.put(1, "rhea");
		servers.put(2, "queeg");
		servers.put(3, "glados");
		servers.put(4, "newyork");
		servers.put(5, "kansas");
		servers.put(6, "maine");
		servers.put(7, "iowa");
		servers.put(8, "missouri");
		servers.put(9, "nevada");
		servers.put(10, "illinois");
		servers.put(11, "utah");
		servers.put(12, "arizona");
		servers.put(13, "idaho");
		servers.put(14, "arkansas");
		servers.put(15, "indiana");
		servers.put(16, "vermont");
		servers.put(17, "alabama");
		servers.put(18, "georgia");

		IPAddress.put("comet", "129.21.34.80");
		IPAddress.put("rhea", "129.21.37.49");
		IPAddress.put("queeg", "129.21.30.37");
		IPAddress.put("glados", "129.21.22.196");
		IPAddress.put("newyork", "129.21.37.16");
		IPAddress.put("kansas", "129.21.37.18");
		IPAddress.put("maine", "129.21.37.1");
		IPAddress.put("iowa", "129.21.37.2");
		IPAddress.put("missouri", "129.21.37.8");
		IPAddress.put("nevada", "129.21.37.25");
		IPAddress.put("illinois", "129.21.37.21");
		IPAddress.put("utah", "129.21.37.11");
		IPAddress.put("arizona", "129.21.37.15");
		IPAddress.put("idaho", "129.21.37.19");
		IPAddress.put("arkansas", "129.21.37.24");
		IPAddress.put("indiana", "129.21.37.10");
		IPAddress.put("vermont", "129.21.37.22");
		IPAddress.put("alabama", "129.21.37.6");
		IPAddress.put("georgia", "129.21.37.20");

	}

	/**
	 * This method is used to insert the file sent by the client into the root
	 * server's directory and update the fileMap accordingly
	 * 
	 * @param name
	 * @param fileName
	 * @param f
	 */
	// public void insertFile(String name, String fileName, File f) throws
	// RemoteException, UnknownHostException {
	public void insertFile(String name, String fileName, File f)
			throws RemoteException, UnknownHostException, IOException,
			FileNotFoundException {

		fileMap.put(fileName, f); // inserting file into the map

		File directory = new File(selfName);
		if (!directory.exists()) {
			// System.out.println("Directory missing");
			directory.mkdir();
			// System.out.println("Directory made");
		}

		FileInputStream fis = new FileInputStream(f);

		InputStream in = fis;

		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String content = "";
		String line;

		while ((line = br.readLine()) != null) {
			content = content + line;
		}
		// br.close();
		// System.out.println("Content = " + content);

		// System.out.println("selfName = " + selfName);
		// System.out.println("fileName = " + fileName);

		File fileReceived = new File("/home/stu9/s8/sxb4298/DS/RFC/" + selfName
				+ "/" + fileName);

		if (!fileReceived.exists()) {
			// System.out.println("File does not exist");
			fileReceived.createNewFile();
			// System.out.println("New file created");
		}

		FileOutputStream fop = new FileOutputStream(fileReceived);
		byte[] contentBytes = content.getBytes();

		fop.write(contentBytes);
		fop.flush();
		fop.close();
		// readFile(fileReceived);

		System.out.println("File inserted at: " + selfName + " by: " + name);
		for (Map.Entry<String, File> me : fileMap.entrySet()) {
			System.out.println("File name: " + me.getKey());
		}
	}

	/**
	 * The main method
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws InterruptedException,
			RemoteException, UnknownHostException {
		System.out.println("Server active");
		Server server = new Server();
		Thread.sleep(6000);
		while (true) {
			// System.out.println("Waiting");
			Thread.sleep(3000);
		}
	}

	/**
	 * This method is used to print the name of the server that connected to it
	 * 
	 * @param name
	 */
	@Override
	public void connected(String name) throws RemoteException {
		System.out.println("Connected: " + name);
	}

	/**
	 * This method is used to search the file requested requested by the client
	 * 
	 * @param clientIP
	 * @param client
	 * @param name
	 * @param fileName
	 * @param level
	 * @param index
	 * @param path
	 */
	@Override
	public void searchFile(String clientIP, String client, String name,
			String fileName, int level, int index, String path)
			throws NoSuchAlgorithmException, NotBoundException, IOException {
		boolean response = false;
		clientIPAddress = clientIP;
		clientName = client;
		if (level == 2) {
			checkLeaf = true;
			checkIntermediate = false;
			checkRoot = false;

		} else if (level == 1) {
			checkIntermediate = true;
			checkLeaf = false;
			checkRoot = false;

		} else if (level == 0) {
			checkRoot = true;
			checkLeaf = false;
			checkIntermediate = false;
		}
		System.out.println("File search initiated by: " + name);
		if (fileCount.containsKey(fileName)) {
			if (fileMap.containsKey(fileName)) {

				Integer val = fileCount.get(fileName);

/*				System.out.println("key = " + fileName + " count = " + val
						+ " server = " + selfName);
*/
				val = val + 1;
				if (val == 5) {
					// replicate file
					// check if current server is a LEAF!!!
					if (checkLeaf == false
							&& (checkRoot == true || checkIntermediate == true)) {
						// replicate to children
						System.out.println("replicating file to children");
						replicateFileToChildren(fileName, level, index);
					}
				}
				fileCount.put(fileName, val);
			}

		} else {
			// fileCount.put(fileName, 1);
			// System.out.println("fileCount does not have fileName");
		}
		if (fileMap.containsKey(fileName)) {
			System.out.println(selfName + " has the file");
			// System.out.println("Level = " + level + " index = " + index);
			/*System.out.println("Client Name: " + clientName + " client IP : "
					+ clientIPAddress);*/
			Registry clientReg = LocateRegistry.getRegistry(clientIPAddress,
					2020);
			CommonInterface cliObj = (CommonInterface) clientReg
					.lookup("client");
			fileNameToBeSent = fileName;
			cliObj.receiveFile(fileMap.get(fileName), selfName);
			System.out.println("Sent file to: " + clientName);
			response = true;
			path = path + "\t" + selfName;
			cliObj.updateClientPath(path);
		} else {
			if (level == 0 && index == 0) {
				System.out.println("Level = " + level + " index = " + index);
				System.out.println("File not found at: " + selfName);
				path = path + "\t" + selfName;
				Registry clientReg = LocateRegistry.getRegistry(
						clientIPAddress, 2020);
				CommonInterface cliObj = (CommonInterface) clientReg
						.lookup("client");
				cliObj.updateClientPath(path);
				cliObj.fileNotFound("File not found");
				response = false;
			} else {
				// System.out.println("calling Check parent");
				path = path + "\t" + selfName;
				checkParent(clientIPAddress, clientName, name, fileName, level,
						index, path);
			}
		}
	}

	/**
	 * This method is used to replicate the popular file to the children of the
	 * parent
	 * 
	 * @param fileName
	 * @param parentLevel
	 * @param parentIndex
	 * @throws NoSuchAlgorithmException
	 * @throws NotBoundException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void replicateFileToChildren(String fileName, int parentLevel,
			int parentIndex) throws NoSuchAlgorithmException,
			NotBoundException, UnknownHostException, IOException {
		if (parentLevel == leafLevel) {
			System.out.println("Parent is a leaf - hence not replicating");
			return;
		}
		int childLevel = parentLevel + 1;
		int childIndexLeft = 2 * parentIndex;
		// System.out.println("parentLevel = " + parentLevel + " parentIndex = "
			//	+ parentIndex);
		// System.out.println("childLevel = " + childLevel);
		// System.out.println("childIndexLeft = " + childIndexLeft);
		int childIndexRight = (2 * parentIndex) + 1;
		// System.out.println("childIndexRight = " + childIndexRight);
		int count = 0;
		int idx = 0;
		String combo = "";
		while (count != 2) {
			// System.out.println("Count = " + count);
			if (count == 0) {
				idx = childIndexLeft;
			} else if (count == 1) {
				idx = childIndexRight;
			}
			combo = fileName + childLevel + idx + "";
			// System.out.println("Combo = " + combo);
			int key = computeHash(combo);
			String val = servers.get(key);
			System.out.println("Replicating file at: " + val);
			String IP = IPAddress.get(val);

			Registry reg = LocateRegistry.getRegistry(IP, 2020);
			CommonInterface childObj = (CommonInterface) reg.lookup("server");
			childObj.connected(selfName);

			File f = fileMap.get(fileName);

			childObj.insertFile(selfName, fileName, f);
			count++;

			childObj.getChildMap(fileName);

		}
	}

	/**
	 * This method is used to compute the parent of a given node and passes on
	 * the file request to the parent
	 * 
	 * @param clientIP
	 * @param clientName
	 * @param name
	 * @param fileName
	 * @param level
	 * @param index
	 * @param path
	 * @throws NoSuchAlgorithmException
	 * @throws NotBoundException
	 * @throws IOException
	 */

	public void checkParent(String clientIP, String clientName, String name,
			String fileName, int level, int index, String path)
			throws NoSuchAlgorithmException, NotBoundException, IOException {

		int parentLevel = level - 1;
		int parentIndex = (int) Math.floor(index / 2);
		// System.out.println("Checking parent");

		/*System.out.println("Parent has level = " + parentLevel + " index = "
				+ parentIndex);*/
		String combo = fileName + parentLevel + parentIndex + "";
		int key = computeHash(combo);
		String val = servers.get(key);
		// System.out.println("connecting to: " + val);
		String IP = IPAddress.get(val);
		Registry r = LocateRegistry.getRegistry(IP, 2020);
		CommonInterface servObj = (CommonInterface) r.lookup("server");
		servObj.connected(selfName);
		servObj.searchFile(clientIP, clientName, name, fileName, parentLevel,
				parentIndex, path);
	}

	/**
	 * This method is used to send the file
	 * 
	 * @param name
	 * @param fileName
	 */
	@Override
	public File sendFile(String name, String fileName) throws RemoteException,
			UnknownHostException {
		System.out.println("Sending file to: " + name + " from: " + selfName);
		return fileMap.get(fileName);
	}

	/**
	 * This method is used to compute the hash of the combination of fileName +
	 * level + index
	 * 
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public int computeHash(String combo) throws NoSuchAlgorithmException {
		MessageDigest msgdgt = MessageDigest.getInstance("SHA1");
		byte[] dataArray = combo.getBytes();
		msgdgt.update(dataArray, 0, dataArray.length);
		BigInteger i = new BigInteger(1, msgdgt.digest());
		int x = i.intValue();
		x = Math.abs(x % 19); // need to change
		// System.out.println("Hashed = " + x);
		return x;
	}

	/**
	 * This method is used to set the IP address and the name of the client
	 * machine
	 * 
	 * @param name
	 * @param IP
	 */
	@Override
	public void setClientNameIP(String name, String IP) throws RemoteException,
			NotBoundException {
		clientName = name;
		clientIPAddress = IP;
		/*System.out.println("Client name is: " + clientName
				+ " and has IP Address = " + clientIPAddress);*/
		Registry reg = LocateRegistry.getRegistry(IP, 2020);
		clientObj = (CommonInterface) reg.lookup("client");
	}

	@Override
	public boolean checkFlagFileFound() throws RemoteException {
		return fileFound;
	}

	@Override
	public void receiveFile(File file, String serverName)
			throws RemoteException, FileNotFoundException, IOException {
	}

	/**
	 * This method is used to set the root of the file inserted by the client on
	 * all the servers
	 * 
	 * @param serverName
	 * @param fileName
	 */
	@Override
	public void setRootOfFile(String serverName, String fileName)
			throws RemoteException {
		rootOfFile = serverName;
		System.out.println("Root of the file: " + fileName + " is: "
				+ rootOfFile);
	}

	/**
	 * This file is used read the file inserted by the client
	 * 
	 * @param f
	 * @throws FileNotFoundException
	 */
	public void readFile(File f) throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		while (sc.hasNext()) {
			System.out.println(sc.nextLine());
		}
		sc.close();
	}

	@Override
	public void getChildMap(String s) throws RemoteException {
		if (!fileCount.containsKey(s)) {
			fileCount.put(s, 0);
		}
	}

	@Override
	public void updateClientPath(String str) throws RemoteException {
	}

	@Override
	public void fileNotFound(String str) throws RemoteException {
	}
}