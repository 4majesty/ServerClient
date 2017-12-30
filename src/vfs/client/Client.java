package vfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import vfs.struct.FileHandle;
import vfs.struct.RemoteFileInfo;

public class Client {
	String masterIP;
	int masterPort;
	
	public Client(String masterIP, int masterPort){
		this.masterIP = masterIP;
		this.masterPort = masterPort;
	}
	
	public boolean create(String remotePath){
		
		return false;
	}
	
	public boolean delete(String remotePath){
		
		return false;
	}
	
	public boolean upload(String localPath, String remotePath){
		new UploadThread(localPath, remotePath, this.masterIP, this.masterPort).start();
		return false;
	}
	
	public boolean download(String localPath, String remotePath){
		new DownloadThread(localPath, remotePath, this.masterIP, this.masterPort).start();
		return false;
	}
	
	public List<RemoteFileInfo> getRemoteFileInfo(){
		
		return null;
	}
	
	public class UploadThread extends Thread{
		private FileOperation fileOp = null;
		private FileHandle remoteFileHandle = null;
		
		private String localPath = null;
		private String remotePath = null;
		
		public UploadThread(String localPath, String remotePath, String masterIP, int masterPort){
			this.localPath = localPath;
			this.remotePath = remotePath;
			
			fileOp = new FileOperation(masterIP, masterPort);
			remoteFileHandle = fileOp.open(this.remotePath, "wr");
		}
		
		public void run(){
			File filein = new File(this.localPath);
			FileInputStream localFis = null;
			try {
				localFis = new FileInputStream(filein);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int bufferSize = 64*1024;
			int readsize = 0;
			
			byte[] buf = new byte[bufferSize];
 			try {
 				while((readsize = localFis.read(buf, 0, buf.length))>0){
 					fileOp.write(remoteFileHandle, buf, readsize);
 				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public class DownloadThread extends Thread{
		private String localPath = null;
		private String remotePath = null;
		
		private FileOperation fileOp = null;
		private FileHandle remoteFileHandle = null;
		
		public DownloadThread(String localPath, String remotePath, String masterIP, int masterPort){
			this.localPath = localPath;
			this.remotePath = remotePath;
			
			fileOp = new FileOperation(masterIP, masterPort);
			remoteFileHandle = fileOp.open(this.remotePath, "r");
		}
		
		public void run(){
			File filein = new File(this.localPath);
			FileOutputStream localFos = null;
			try {
				localFos = new FileOutputStream(filein);  
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int bufferSize = 64*1024;
			int readsize = 0;
			
			byte[] buf = new byte[bufferSize];
			try {
				while((readsize = fileOp.read(remoteFileHandle, buf, bufferSize)) > 0){
					localFos.write(buf, 0, readsize);
					localFos.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
    public static void main(String[] args) {
//        List<Integer> list = new LinkedList<Integer>();
//        
//        list.add(1);
//        list.add(2);
//        
//        System.out.println(list);
//         
//        list.remove(new Integer(2));
//        
//        System.out.println(list);
//        
//        System.out.println('\0');
    	Client client = new Client("127.0.0.1", 8807);
//    	client.upload("/Users/zsy/Documents/workspace/Java/test.txt", "/Users/zsy/Documents/workspace/Java/abc1.txt");
//    	client.upload("/Users/zsy/Documents/workspace/Java/test.txt", "/Users/zsy/Documents/workspace/Java/abc2.txt");
    	
    	client.download("/Users/zsy/Documents/workspace/Java/download.txt", "/Users/zsy/Documents/workspace/Java/abc1.txt");
    }
	
}
