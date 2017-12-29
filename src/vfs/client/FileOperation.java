package vfs.client;

import java.net.Socket;
import java.util.List;

import vfs.struct.FileHandle;
import vfs.struct.RemoteFileInfo;

public class FileOperation {
//	private static FileOperation self = null;
//	
	public FileOperation(){
		
	}
//	
//	public static FileOperation instance(){
//		if(FileOperation.self == null){
//			FileOperation.self = new FileOperation();
//		}
//		
//		return FileOperation.self;
//	}
	
	public boolean mkdir(String dirName){
		
		return false;
	}
	
	public boolean creat(String remotePath){
		
		return false;
	}
	
	public FileHandle open(String remotePath, String mode){
		
		return null;
	}
	
	public int tell(FileHandle handle){
		return handle.offset;
	}
	
	public int write(FileHandle handle, byte[] buf, int nbyte){
		
		return 0;
	}
	
	public int read(FileHandle handle, byte[] buf, int nbyte){
		
		return 0;
	}
	
	public boolean setFileSize(String masterIP, int fileSize){
		
		return false;
	}
	
	private boolean writeChunk(String slaveIP, int chunkId, byte[] buf, int startPos, int writeLen){
		
		return false;
	}
	
	private boolean readChunk(String slaveIP, int chunkId, byte[] buf){
		
		return false;
	}
	
	private boolean appendChunk(String slaveIP, int chunkId, byte[] buf, int nbyte){
		
		return false;
	}
}



