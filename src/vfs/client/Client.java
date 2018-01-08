package vfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import vfs.struct.FileHandle;
import vfs.struct.FileNode;
import vfs.struct.RemoteFileInfo;

public class Client {
	String masterIP = null;
	int masterPort = 0;
	FileOperation fileOp = null;
//	FileNode rootFileNode = null;
	FileHierarchy fileHierachy = null; // TODO when fileHierachy expire, refresh fileHierachy
	
	UploadThread currUploadThread = null;
	DownloadThread currDownloadThread = null;
	
	public Client(String masterIP, int masterPort){
		this.masterIP = masterIP;
		this.masterPort = masterPort;
		
		this.fileOp = new FileOperation(this.masterIP, this.masterPort);
//		this.rootFileNode = this.fileOp.getFileNode();
		
		this.fileHierachy = this.fileOp.getFileHierarchy();
	}
	
	public boolean create(String remotePath, boolean isDir){
		boolean res = false;
		
		if(this.fileHierachy == null){
			this.fileHierachy = this.fileOp.getFileHierarchy();
		}
		
		String path = this.getFileDir(remotePath);
		String dirName = this.getFileName(remotePath);
		if(isDir){
			res = this.fileOp.mkdir(remotePath);
		}else{
			res = this.fileOp.creat(remotePath);
		}
		
		if(res){
			if(isDir){
				this.fileHierachy.mkdir(path, dirName);
			}else{
				this.fileHierachy.openFile(path, dirName);
			}
		}
		
		return res;
	}
	
	public boolean delete(String remotePath){
		String path = this.getFileDir(remotePath);
		String dirName = this.getFileName(remotePath);
		
		if(this.fileHierachy == null){
			this.fileHierachy = this.fileOp.getFileHierarchy();
		}
		
		this.fileHierachy.remove(path, dirName);
		this.fileOp.remove(remotePath);
//		this.fileHierachy.remove(path, dirName);
		
		return false;
	}
	
	public void upload(String localPath, String remotePath){
		String path = this.getFileDir(remotePath);
		String name = this.getFileName(remotePath);
		this.fileHierachy.openFile(path, name);
		
		currUploadThread = new UploadThread(localPath, remotePath, this.masterIP, this.masterPort);
		currUploadThread.start();
//		rootFileNode = this.fileOp.getFileNode();

	}
	
	public void download(String localPath, String remotePath){
		currDownloadThread = new DownloadThread(localPath, remotePath, this.masterIP, this.masterPort);
		currDownloadThread.start();
	}
	
	public List<RemoteFileInfo> getRemoteFileInfo(String remotePath){
		List<RemoteFileInfo> fileInfoList = new LinkedList<RemoteFileInfo>();
		
//		String path = this.getFileDir(remotePath);
//		String dirName = this.getFileName(remotePath);
		
		FileNode fileNode = this.fileHierachy.OpenDir(remotePath);
		if(fileNode.child ==  null){
			return fileInfoList; 
		}else{
			FileNode currNode = fileNode.child;
			while(currNode != null){
				RemoteFileInfo currFileInfo = new RemoteFileInfo();
				currFileInfo.fileName = currNode.fileName;
				currFileInfo.fileType = currNode.isDir?1:0;
				currFileInfo.remotePath = remotePath;
				
				fileInfoList.add(currFileInfo);
				currNode = currNode.brother;
			}
		}
		
		return fileInfoList;
	}
	
	
	public float getUploadRate(){
		if(currUploadThread == null){
			return 1;
		}
		
		return currUploadThread.getProcessRate();
	}
	
	public float getDownloadRate(){
		if(currDownloadThread == null){
			return 1;
		}
		return currDownloadThread.getProcessRate();
	}
	
	public class UploadThread extends Thread{
		private FileOperation fileOp = null;
		private FileHandle remoteFileHandle = null;
		
		private String localPath = null;
		private String remotePath = null;
		
		private float processRate = 0.f;
		
		public UploadThread(String localPath, String remotePath, String masterIP, int masterPort){
			this.localPath = localPath;
			this.remotePath = remotePath;
			this.processRate = 0.f;
			
			fileOp = new FileOperation(masterIP, masterPort);
			remoteFileHandle = fileOp.open(this.remotePath, "wr");

		}
		
		public float getProcessRate(){
			return this.processRate;
		}
		
		public void run(){
			File filein = new File(this.localPath);
			if(!filein.exists()){
				System.out.println("no path: " + localPath + " on the computer!");
				return;
			}
			long fileSize = filein.length();
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
 					this.processRate += readsize/Math.max(1.f, fileSize);
 				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 			this.processRate = 1.f;
 			
 			try {
				localFis.close();
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
		
		private float processRate = 0.f;
		
		public DownloadThread(String localPath, String remotePath, String masterIP, int masterPort){
			this.localPath = localPath;
			this.remotePath = remotePath;
			this.processRate = 0.f;
			
			fileOp = new FileOperation(masterIP, masterPort);
			remoteFileHandle = fileOp.open(this.remotePath, "r");
		}
		
		public float getProcessRate(){
			return this.processRate;
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
			
			long fileTotalSize = fileOp.getFileSize(remoteFileHandle);
			
			int bufferSize = 64*1024;
			int readsize = 0;
			
			byte[] buf = new byte[bufferSize];
			try {
				while((readsize = fileOp.read(remoteFileHandle, buf, bufferSize)) > 0){
					localFos.write(buf, 0, readsize);
					localFos.flush();
					this.processRate += readsize/Math.max(1.f, fileTotalSize);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.processRate = 1.f;
			
			try {
				localFos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getFileDir(String filePath) {
		String[] names = filePath.split("[/\\\\]");
		StringBuilder path = new StringBuilder();
		for(int i = 0; i < names.length-1; ++i){
			path = path.append(names[i] + "/");
		}
		
		return path.toString();
	}
	
	private String getFileName(String filePath){
		String[] names = filePath.split("[/\\\\]");
		return names[names.length-1];
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
    	String ipAddr = "localhost";
    	
    	Client client = new Client(ipAddr, 8877);
    	
//    	client.create("vfs/zsy", true);
////    	client.delete("vfs/b");
//    	client.getRemoteFileInfo("vfs");
//    	client.delete("vfs/b");
//    	client.upload("/Users/zsy/Documents/workspace/Java/test.txt", "/Users/zsy/Documents/workspace/Java/abc1.txt");
//    	client.upload("/Users/zsy/Documents/workspace/Java/test.txt", "/Users/zsy/Documents/workspace/Java/abc2.txt");
    	
    	client.upload("/Users/zsy/Documents/workspace/Java/test-bak.txt", "vfs/b");
    	client.download("/Users/zsy/Documents/workspace/Java/dtest.txt", "vfs/b");
    }
	
}
