package vfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	public boolean download(String remotePath){
		
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
			
			fileOp = new FileOperation("127.0.0.1", 8807);
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
		
//		private FileOperation fileOp = null;
//		private FileHandle remoteFileHandle = null;
//		private FileInputStream localFis = null;
//		Socket socket = null;
//		
//		private String localPath = null;
//		private String remotePath = null;
//		
//		static final int CHUNK_SIZE = 64*1024*1024;
//		
//		public UploadThread(String localPath, String remotePath, String masterIP, int masterPort){
//			this.localPath = localPath;
//			this.remotePath = remotePath;
//			
//			File localFile = new File(this.localPath);
//			try {
//				localFis = new FileInputStream(localFile);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			try {
//				socket = new Socket(masterIP, masterPort);
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			fileOp = new FileOperation();
//			remoteFileHandle = fileOp.open(this.remotePath, "wr");
//			
//		}
//		
//		public void run(){
//			int bufferSize = 100*1024;
//			byte[] buf = new byte[bufferSize];
//			OutputStream out = null;
//			int readsize = 0;
//			
//			try {
//				out = socket.getOutputStream();
//				
//				// file name
//				byte[] file = new byte[256];
//				byte[] tfile = this.remotePath.getBytes();
//				for(int i=0;i<tfile.length;i++){
//					file[i] = tfile[i];
//		        }
//		        file[tfile.length] = '\0';
//		        out.write(file,0,file.length);
//		        System.out.println("filename len: "+file.length);
//		        
//		        //file size
//		        File filein = new File(this.localPath);
//		        byte[] size = new byte[64];
//		        byte[] tsize = (""+filein.length()).getBytes();
//		        for(int i=0;i<tsize.length;i++){
//		            size[i] = tsize[i];
//		        }
//		        size[tsize.length] = '\0';
//		        out.write(size,0,size.length);
//		        System.out.println("filesize len: "+size.length);
//		        
//		        while((readsize = localFis.read(buf, 0, buf.length))>0){
//		        	out.write(buf,0,readsize);
//				    out.flush();
//				}
//				
//				localFis.close();
//				out.close();
//				
//				socket.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
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
    	Client client = new Client("127.0.0.1", 8877);
    	client.upload("/Users/zsy/Documents/workspace/Java/test.txt", "/Users/zsy/Documents/workspace/Java/abc1.txt");
//    	client.upload("/Users/zsy/Documents/workspace/Java/test.txt", "/Users/zsy/Documents/workspace/Java/abc2.txt");
    }
	
}
