import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
/**
 * This is the CommonInterface that is shared by 
 * both the Client and Server classes for the 
 * remote method invocation
 *  
 * @author Swati Bhartiya (sxb4298)
 *
 */
public interface CommonInterface extends java.rmi.Remote {
	public void insertFile(String name, String fileName, File f) throws RemoteException, UnknownHostException, IOException;
	public void connected(String name) throws RemoteException;
	public void searchFile(String clientIP, String clientName, String name, String fileName, int level, int index, String path) throws RemoteException, UnknownHostException, NoSuchAlgorithmException, NotBoundException, IOException;
	public File sendFile(String name, String fileName) throws RemoteException, UnknownHostException;
	public void setClientNameIP(String name, String IP) throws RemoteException, NotBoundException;
	public boolean checkFlagFileFound() throws RemoteException;
	public void receiveFile(File file, String serverName) throws RemoteException, FileNotFoundException, IOException;
	public void setRootOfFile(String serverName, String fileName) throws RemoteException;
	public void updateClientPath(String str) throws RemoteException;
	public void fileNotFound(String str) throws RemoteException;
	public void getChildMap(String s) throws RemoteException;
}
