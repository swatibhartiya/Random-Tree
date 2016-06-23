import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
/**
 * This is the class for the Client 
 * that is used to insert a file
 * and initiate the search for a file
 * 
 * @author Swati Bhartiya (sxb4298)
 *
 */
public class Client extends UnicastRemoteObject implements CommonInterface {
	private static final long serialVersionUID = 1L;
	String fileName = "";
	int level;
	int index;
	File fileReceived;
	HashMap<String, File> fileReceivedMap = new HashMap<>();
	HashMap<Integer, String> servers = new HashMap<Integer, String>();
	HashMap<String, String> IPAddress = new HashMap<String, String>();

	String fileNameToBeInserted;

	String selfName;
	String selfIP;

	
	String path = "";
	
	/**
	 * This is the constructor of the Client
	 * 
	 * @throws RemoteException
	 */
	public Client() throws RemoteException {
		super();
		Registry reg = LocateRegistry.createRegistry(2020);
		reg.rebind("client", this);

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
	 * This method is used to update the path 
	 * taken in order to find the file
	 * 
	 * @param str
	 */
	@Override
	public void updateClientPath(String str){
		path = str;
		System.out.println("Path followed: " + path);
	}

	/**
	 * This method is used to compute the hash of
	 * the combination of fileName + level + index
	 * 
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public int computeHash(String file) throws NoSuchAlgorithmException {
		MessageDigest msgdgt = MessageDigest.getInstance("SHA1");
		byte[] dataArray = file.getBytes();
		msgdgt.update(dataArray, 0, dataArray.length);
		BigInteger i = new BigInteger(1, msgdgt.digest());
		int x = i.intValue();
		x = Math.abs(x % 19); // need to change
		System.out.println("Hashed = " + x);
		return x;
	}

	/**
	 * This is the main method
	 * @param args
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NotBoundException
	 */
	public static void main(String[] args) throws IOException,
			NoSuchAlgorithmException, NotBoundException {
		Scanner sc = new Scanner(System.in);
		Client c = new Client();
		Random rndm = new Random();

		int key;
		String val;
		String ans = "";
		String combo = "";
		// boolean reply = false;
		String isRoot = "";

		c.selfName = InetAddress.getLocalHost().getHostName();
		c.selfIP = InetAddress.getLocalHost().getHostAddress();
		while (true) {
			System.out.println("Would you like to insert a file?"
					+ "\n\nEnter Y for Yes");
			ans = sc.next();
			if (ans.equalsIgnoreCase("Y")) {
				System.out.println("Enter file name :");
				c.fileName = sc.next();
				// System.out.println("New file created");
				File f = new File(c.fileName);
				c.level = 0;
				c.index = 0;
				combo = c.fileName + c.level + c.index + ""; // inserting file
																// at root (0,
																// 0)
				// System.out.println("Combo for file insertion = " + combo);
				key = c.computeHash(combo);
				val = c.servers.get(key);
				System.out.println("File has to be inserted at : " + val);
				isRoot = val;
				System.out.println("Root is: " + isRoot);
				String IP = c.IPAddress.get(val);
				System.out.println("Connecting to : " + val);
				Registry aRegistry = LocateRegistry.getRegistry(IP, 2020);
				CommonInterface servObj = (CommonInterface) aRegistry
						.lookup("server");
				servObj.connected(c.selfName);

				c.fileNameToBeInserted = c.fileName;

				servObj.insertFile(c.selfName, c.fileName, f);
				ans = "";
				System.out.println("Sent file to server from client");
				informRootToAllServers(c.IPAddress, isRoot, c.selfName,
						c.fileName, c.selfIP);

			} else {
				
				System.out.println("Would you like to search "
						+ "for a file\n\nEnter Y for Yes");
				ans = sc.next();
				if (ans.equalsIgnoreCase("Y")) {
					System.out.println("User wants to search for a file");
					System.out
							.println("Enter the name of the file you want to search for: ");
					c.fileName = sc.next();
					c.level = 2; // client connects to leaf node ---- NEED TO
									// CHANGE
					while (true) {
						c.index = rndm.nextInt((4 - 0)) + 0; // need to change
						// System.out.println("Enter a number in the range of 0 - 1: ");
						// c.index = sc.nextInt();
						// System.out.println("node index = " + c.index);
						combo = c.fileName + c.level + c.index + "";
						// System.out.println("Combo for file search = " + combo);

						key = c.computeHash(combo);
						val = c.servers.get(key);
						// System.out.println("Leaf node is : " + val);
						if (!val.equals(isRoot)) {
							break;
						}
					}
					System.out.println("Leaf node is : " + val);
					// connect to leaf node
					String IP = c.IPAddress.get(val);
					System.out.println("Connecting to: " + val);
					
					c.path = c.selfName;
					
					Registry aRegistry = LocateRegistry.getRegistry(IP, 2020);
					CommonInterface servObj = (CommonInterface) aRegistry
							.lookup("server");
					servObj.setClientNameIP(c.selfName, c.selfIP);
					/*System.out.println("c.selfIP = " + c.selfIP
							+ "\n c.selfName = " + c.selfName
							+ "\n c.selfName = " + c.selfName
							+ "\n c.fileName = " + c.fileName + "\n c.level = "
							+ c.level + "\n c.index = " + c.index);*/
					servObj.searchFile(c.selfIP, c.selfName, c.selfName,
							c.fileName, c.level, c.index, c.path);

					// contact server with the file - get IP address and name
					ans = "";
				}
			}
		}
	}

	/**
	 * 
	 * @param str
	 * @throws RemoteException
	 */
	@Override
	public void fileNotFound(String str) throws RemoteException{
		System.out.println(str);
	}
	/**
	 * This method is used to inform all the servers 
	 * in the serversMap the root of the file inserted
	 * by the client
	 *  
	 * @param hm
	 * @param isRoot
	 * @param name
	 * @param fileName
	 * @param clientIPAddress
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws FileNotFoundException
	 */
	private static void informRootToAllServers(HashMap<String, String> hm,
			String isRoot, String name, String fileName, String clientIPAddress)
			throws RemoteException, NotBoundException, FileNotFoundException {
		for (Map.Entry<String, String> me : hm.entrySet()) {
			Registry aRegistry = LocateRegistry
					.getRegistry(me.getValue(), 2020);
			CommonInterface servObj = (CommonInterface) aRegistry
					.lookup("server");
			
			servObj.connected(name);
			servObj.setRootOfFile(isRoot, fileName);
		}
	}

	@Override
	public void connected(String name) throws RemoteException {
	}

	@Override
	public void insertFile(String name, String fileName, File f)
			throws RemoteException, UnknownHostException {
	}

	@Override
	public void searchFile(String clientIP, String clientName, String name,
			String fileName, int level, int index, String path) throws RemoteException,
			UnknownHostException, NoSuchAlgorithmException, NotBoundException {
	}

	@Override
	public File sendFile(String name, String fileName) throws RemoteException,
			UnknownHostException {
		return null;
	}

	@Override
	public void setClientNameIP(String name, String IP) throws RemoteException, NotBoundException {
	}

	@Override
	public boolean checkFlagFileFound() throws RemoteException {
		return false;
	}

	/**
	 * This method is used to receive the file 
	 * sent by the server that found the file
	 * into the client's directory
	 * 
	 * @param file
	 * @param serverName
	 */
	@Override
	public void receiveFile(File file, String serverName)
			throws RemoteException, FileNotFoundException, IOException {
		if (!fileName.equals("")) {
			fileReceivedMap.put(fileName, file);
			System.out.println("FileName: " + fileName + " inserted by: "
					+ serverName);
			
			FileInputStream fis = new FileInputStream(file);

			File directory = new File(selfName);
			if(!directory.exists()){
				// System.out.println("Directory missing");
				directory.mkdir();
				// System.out.println("Directory made");
			}
			
			InputStream in = fis;

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String content = "";
			String line;

			while ((line = br.readLine()) != null) {
				content = content + line;
			}
			// br.close();
			// System.out.println("Content = " + content);
			
			// System.out.println("selfName = "+selfName);
			// System.out.println("fileName = "+fileName);
			File fileReceived = new File("/home/stu9/s8/sxb4298/DS/RFC/" + selfName + "/"
					+ fileName);
			// fileReceived = f;
			if(!fileReceived.exists()){
				// System.out.println("File does not exist");
				fileReceived.createNewFile();
				// System.out.println("New file created");
			}
			FileOutputStream fop = new FileOutputStream(fileReceived);
			byte[] contentBytes = content.getBytes();
			
			fop.write(contentBytes);
			fop.flush();
			fop.close();

			// read(fileReceived);
			
			
		} else {
			System.out.println("File name == null isssssssss :(");
		}
	}
	@Override
	public void getChildMap(String s) throws RemoteException{
		
		
	}
	/**
	 * This method is used to read the file
	 * 
	 * @param fileReceived
	 * @throws FileNotFoundException
	 */
	public void read(File fileReceived) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(fileReceived);
		while (sc.hasNext()) {
			System.out.println(sc.nextLine());
		}
		sc.close();

	}

	@Override
	public void setRootOfFile(String serverName, String fileName)
			throws RemoteException {

	}	
}