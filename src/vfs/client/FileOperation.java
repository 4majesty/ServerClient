package vfs.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import vfs.struct.ChunkInfo;
import vfs.struct.FileHandle;
import vfs.struct.FileNode;
import vfs.struct.RemoteFileInfo;
import vfs.struct.VSFProtocols;

public class FileOperation {
	static final int CHUNK_SIZE = 64*1024; // *1024;
	static final int UPLOAD_BUFFER_SIZE = 8*1024;
	static final int DOWNLOAD_BUFFER_SIZE = 8*1024;
	
	private String masterIP = null;
	private int masterPort = 0;
	
	public FileOperation(String masterIP, int masterPort){
		this.masterIP = masterIP;
		this.masterPort = masterPort;
	}
	
	public FileHierarchy getFileHierarchy(){
		FileHierarchy fileHierarchy = null;
		try {
			Socket socket = new Socket(this.masterIP, this.masterPort);
			OutputStream out = socket.getOutputStream();
			
			// protocol id 
			byte[] protocolBuf = new byte[8];
			this.writeInt(out, protocolBuf, VSFProtocols.GET_FILE_NODE);
			
			//response from server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			String ret = input.readUTF();     
	        System.out.println("getFileHierarchy response code: " + ret);
	        
	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
//	        	File tfile = new File("/Users/zsy/Documents/workspace/Java/VFSClient/file.json");
//	        	FileInputStream fileIn = new FileInputStream(tfile);
//	        	InputStreamReader inputStreamReader = new InputStreamReader(fileIn, "UTF-8");
//	        	BufferedReader reader = new BufferedReader(inputStreamReader);
//	        	
//	        	String jsonStr = "";
//	        	String tempString = null;
//	        	while((tempString = reader.readLine()) != null){
//	        		jsonStr += tempString;
//	        	}
//	        	reader.close();
	        
	        	String jsonStr = readJSONString(input);
	        	JSONObject config = new JSONObject(jsonStr);
	        	fileHierarchy = new FileHierarchy(config);
	        }
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fileHierarchy;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fileHierarchy;
		}
		
		return fileHierarchy;
	}
	
//	public FileNode getFileNode(){
//		FileNode fileNode = new FileNode();
//		
//		try {
//			Socket socket = new Socket(this.masterIP, this.masterPort);
//			OutputStream out = socket.getOutputStream();
//			
//			// protocol id 
//			byte[] protocolBuf = new byte[8];
//			this.writeInt(out, protocolBuf, VSFProtocols.GET_FILE_NODE);
//			
//			//response from server
//			DataInputStream input = new DataInputStream(socket.getInputStream());
//			String ret = input.readUTF();     
//	        System.out.println("response code: " + ret);
//	        
//	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
//	        	// TODO get json file and parse the file node
//	        	String jsonStr = new String();
//	        	JSONObject config = new JSONObject(jsonStr);
//	        	fileNode.parseJSON(config, null);
//	        }
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return fileNode;
//	}
	
	public boolean mkdir(String remoteDir){
		try {
			Socket socket = new Socket(this.masterIP, this.masterPort);
			OutputStream out = socket.getOutputStream();
			
			// protocol id
			byte[] protocolBuf = new byte[8];
			this.writeInt(out, protocolBuf, VSFProtocols.MK_DIR);
			
			// file location
//			byte[] locationBuf = new byte[256];
//			this.writeString(out, locationBuf, remotePath);
			DataOutputStream dataOut = new DataOutputStream(out);
			byte[] locationBuf = remoteDir.getBytes();
			dataOut.writeInt(locationBuf.length);
			dataOut.write(locationBuf);
			
			//response from server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			String ret = input.readUTF();     
	        System.out.println("mkdir response code: " + ret);
	        
	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
	        	return true;
	        }else{
	        	return false;
	        }

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean creat(String remotePath){
		FileHandle handle = this.open(remotePath, "w+");
		if (handle == null){
			return false;
		}else{
			return true;
		}
//		try {
//			Socket socket = new Socket(this.masterIP, this.masterPort);
//			OutputStream out = socket.getOutputStream();
//			
//			// protocol id
//			byte[] protocolBuf = new byte[8];
//			this.writeInt(out, protocolBuf, VSFProtocols.CREATE_FILE);
//			
////			// remotePath
////			byte[] pathBuf = new byte[256];
////			this.writeString(out, pathBuf, remotePath);
//			
//			// file location
////			byte[] locationBuf = new byte[256];
////			this.writeString(out, locationBuf, remotePath);
//			DataOutputStream dataOut = new DataOutputStream(out);
//			byte[] locationBuf = remotePath.getBytes();
//			dataOut.writeInt(locationBuf.length);
//			dataOut.write(locationBuf);
//			
//			//response from server
//			DataInputStream input = new DataInputStream(socket.getInputStream());
//			String ret = input.readUTF();     
//	        System.out.println("response code: " + ret);
//	        
//	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
//	        	return true;
//	        }else{
//	        	return false;
//	        }
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
			
	}
	
	public FileHandle open(String remotePath, String mode){
		// TODO file privilege
		FileHandle tempHandle = new FileHandle(2); // 2 for r&w
		
		try {
			Socket socket = new Socket(this.masterIP, this.masterPort);
			OutputStream out = socket.getOutputStream();
			
			// protocol id
			byte[] protocolBuf = new byte[8];
			this.writeInt(out, protocolBuf, VSFProtocols.OPEN_FILE);
			
			// file location
//			byte[] locationBuf = new byte[256];
//			this.writeString(out, locationBuf, remotePath);
			DataOutputStream dataOut = new DataOutputStream(out);
			byte[] locationBuf = remotePath.getBytes();
			dataOut.writeInt(locationBuf.length);
			dataOut.write(locationBuf);
			
			//response from server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			String ret = input.readUTF();     
	        System.out.println("open response code: " + ret);
	        
	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
//	        	int objLen = input.readInt();
//	        	byte[] objBytes = new byte[objLen];
//	        	this.readBytes(input, objBytes, objLen);
	        	String JSONStr = readJSONString(input);
	        	JSONObject jsonObj = new JSONObject(JSONStr);
	        	
	        	tempHandle.parseJSON(jsonObj);
	        }else{
	        	return null;
	        }
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return tempHandle;
	}
	
	public boolean remove(String remotePath){
		try {
			Socket socket = new Socket(this.masterIP, this.masterPort);
			OutputStream out = socket.getOutputStream();
			
			// protocol id
			byte[] protocolBuf = new byte[8];
			this.writeInt(out, protocolBuf, VSFProtocols.REMOVE_FILE);
			
			// file location
//			byte[] locationBuf = new byte[256];
//			this.writeString(out, locationBuf, remotePath);
			DataOutputStream dataOut = new DataOutputStream(out);
			byte[] locationBuf = remotePath.getBytes();
			dataOut.writeInt(locationBuf.length);
			dataOut.write(locationBuf);
			
			//response from server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			String ret = input.readUTF();     
	        System.out.println("remove response code: " + ret);
	        
	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
	        	return true;
	        }
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	public int tell(FileHandle handle){
		return handle.offset;
	}
	
	public int write(FileHandle handle, byte[] buf, int nbyte){
		if(nbyte <= 0){
			return 0;
		}
		
		int offset = handle.offset;
		int firstChunkIndex = (int) Math.floor(offset/CHUNK_SIZE);
		int firstChunkOffset = (int) offset%CHUNK_SIZE;
		
		int nbyteLeft = Math.min(buf.length, nbyte);
		int chunkNum = 0;
		if(nbyteLeft + firstChunkOffset <= CHUNK_SIZE){
			chunkNum = 1;
		}else{
			chunkNum = 1 + (int) Math.ceil((nbyteLeft - (CHUNK_SIZE - firstChunkOffset))/CHUNK_SIZE);
		}
		
		
		int maxChunIndx = handle.getMaxChunkIndex();
		if(maxChunIndx < firstChunkIndex + chunkNum - 1){
			handle = this.addChunk(handle, firstChunkIndex + chunkNum - 1 - maxChunIndx);
			if(handle == null){
				return -1;
			}
		}
		System.out.println("handle.getMaxChunkIndex(): " + handle.getMaxChunkIndex());
		
		byte[] chunkBuf = new byte[CHUNK_SIZE];
		int writeByteCount = 0;
		
		for(int writeChunkCount = 0; writeChunkCount < chunkNum; ++writeChunkCount){
			int writeByteNum = 0;
			ChunkInfo currentChunk = handle.getChunkInfoByIndex(firstChunkIndex + writeChunkCount);
			if(currentChunk == null){
				System.out.println("error: invalid file handle!");
				return -1;
			}
			
			int writeLen = Math.min(nbyteLeft, CHUNK_SIZE);

			for(int i = 0; i < writeLen; ++i){
				chunkBuf[i] = buf[writeByteCount+i];
			}
			try {
				if(writeChunkCount == 0){
					writeByteNum = writeChunk(currentChunk, firstChunkOffset, chunkBuf, writeLen);
				
				}else{
					writeByteNum = writeChunk(currentChunk, 0, chunkBuf, writeLen);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
			nbyteLeft -= writeByteNum;
			writeByteCount += writeByteNum;
		}
		
		handle.offset += writeByteCount;
		return writeByteCount;
	}
	
	public int read(FileHandle handle, byte[] buf, int nbyte){
		if(nbyte <= 0){
			return 0;
		}

		int offset = handle.offset;
		int firstChunkIndex = (int) Math.floor(offset/CHUNK_SIZE);
		int firstChunkOffset = (int) offset%CHUNK_SIZE;
		int nbyteLeft = Math.min(nbyte, buf.length);
		
		int chunkNum = 0;
		if(nbyteLeft + firstChunkOffset <= CHUNK_SIZE){
			chunkNum = 1;
		}else{
			chunkNum = 1 + (int) Math.ceil((nbyteLeft - (CHUNK_SIZE - firstChunkOffset))/CHUNK_SIZE);
		}
		
		byte[] chunkBuf = new byte[CHUNK_SIZE];
		int readByteCount = 0;
		for(int readChunkCount = 0; readChunkCount < chunkNum; ++readChunkCount){
			int readByteNum = 0;
			ChunkInfo currentChunk = handle.getChunkInfoByIndex(firstChunkIndex + readChunkCount);
			if(currentChunk == null){
				handle.offset += readByteCount;
				return readByteCount;  // read range overflow
			}
			
			int readLen = Math.min(nbyteLeft, CHUNK_SIZE);

			try {
				if(readChunkCount == 0){
					readByteNum = readChunk(currentChunk, firstChunkOffset, chunkBuf, readLen);
				}else{
					readByteNum = readChunk(currentChunk, 0, chunkBuf, readLen);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i = 0; i < readByteNum; ++i){
				buf[readByteCount+i] = chunkBuf[i];
			}
			
			readByteCount += readByteNum;
			nbyteLeft -= readByteNum;
		}
		
		handle.offset += readByteCount;
		return readByteCount;
	}
	
	public FileHandle setFileSize(FileHandle handle , int fileSize){
		try {
			Socket socket = new Socket(this.masterIP, this.masterPort);
			OutputStream out = socket.getOutputStream();
			
			// protocol id
			byte[] protocolBuf = new byte[8];
			this.writeInt(out, protocolBuf, VSFProtocols.RESIZE_FILE);
			
			DataOutputStream dataOut = new DataOutputStream(out);
			// file size
//			byte[] sizeBuf = new byte[64];
//			this.writeInt(out, sizeBuf, fileSize);
			dataOut.writeInt(fileSize);
			
			// file location
			String fileLocation = handle.fileInfo.remotePath + "/" + handle.fileInfo.fileName;
			byte[] locationBuf = new byte[256];
			this.writeString(out, locationBuf, fileLocation);
			
			//response from server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			String ret = input.readUTF();     
	        System.out.println("file resize response code: " + ret);
	        
	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
	        	// TODO get json file and parse the filehandle
	        }

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return handle;
	}
	
	public FileHandle addChunk(FileHandle handle , int addNum){
		try {
			Socket socket = new Socket(this.masterIP, this.masterPort);
			OutputStream out = socket.getOutputStream();
			
			// protocol id
			byte[] protocolBuf = new byte[8];
			this.writeInt(out, protocolBuf, VSFProtocols.ADD_CHUNK);
			
			DataOutputStream dataOut = new DataOutputStream(out);
			
			// file location
			String fileLocation = handle.fileInfo.remotePath + "/" + handle.fileInfo.fileName;
			
//			PrintWriter writer = new PrintWriter(out);
//			writer.println(fileLocation);
			
//			byte[] locationBuf = new byte[256]; 
//			this.writeString(out, locationBuf, fileLocation);
			
			byte[] locationBuf = fileLocation.getBytes();
			dataOut.writeInt(locationBuf.length);
			dataOut.write(locationBuf);
			
			// chunk number
//			byte[] numBuf = new byte[64];
//			this.writeInt(out, numBuf, addNum);
			dataOut.writeInt(addNum);
			
			//response from server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			String ret = input.readUTF();
	        System.out.println("add chunk response code: " + ret);
	        
	        if (VSFProtocols.MESSAGE_OK.equals(ret)){
//	        	int objLen = input.readInt();
//	        	byte[] objBytes = new byte[objLen];
//	        	this.readBytes(input, objBytes, objLen);
	        	String JSONStr = readJSONString(input);
	        	JSONArray jsonArr = new JSONArray(JSONStr);
	        	handle.chunkList = new LinkedList<ChunkInfo>();
	        	for(int i = 0; i < jsonArr.length(); ++i){
	        		ChunkInfo curr = new ChunkInfo();
	        		curr.parseJSON(jsonArr.getJSONObject(i));
	        		handle.chunkList.add(curr);
	        	}
	        }else{
	        	System.out.println("fail to addchunk, error code: " + ret);
	        	handle = null;
	        }

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return handle;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return handle;
		}
		
		return handle;
	}
	
	public long getFileSize(FileHandle handle){
		List<ChunkInfo> chunkList = handle.chunkList;
		int fileSize = 0;
		for(int i = 0; i < chunkList.size(); ++i){
			fileSize += CHUNK_SIZE - chunkList.get(i).chunkLeft;
		}
		
		return fileSize;
	}
	
	private int writeChunk(ChunkInfo chunkInfo, int startPos, byte[] buf, int writeLen) throws UnknownHostException, IOException{
		writeLen = Math.min(CHUNK_SIZE-startPos, Math.min(buf.length, writeLen));
		if(writeLen <= 0){
			return 0;
		}
		
		Socket socket = new Socket(chunkInfo.slaveIP, chunkInfo.port);
		OutputStream out = socket.getOutputStream();
		System.out.println("slave ip: " + chunkInfo.slaveIP + ",port: " + chunkInfo.port);
		
		// protocol id
		byte[] protocolBuff = new byte[8];
		byte[] protocolBytes = (Integer.toString((VSFProtocols.WRITE_CHUNK))).getBytes();
		for(int i = 0; i < protocolBytes.length; ++i){
			protocolBuff[i] = protocolBytes[i];
		}
		protocolBuff[protocolBytes.length] = '\0';
		out.write(protocolBuff, 0, protocolBuff.length);
		System.out.println("protocol id: " + protocolBuff);
		
		
		DataOutputStream dataOut = new DataOutputStream(out);
		// chunk_id
		dataOut.writeInt(chunkInfo.chunkId);
		
//		byte[] sizeBuff = new byte[64];
//		byte[] sizeBytes = (Integer.toString(chunkInfo.chunkId)).getBytes();
//		for(int i = 0; i < sizeBytes.length;++i){
//			sizeBuff[i] = sizeBytes[i];
//        }
//		sizeBuff[sizeBytes.length] = '\0';
//		out.write(sizeBuff, 0, sizeBuff.length);
//		System.out.println("chunk_id: " + sizeBuff);
		
		//offset
		dataOut.writeInt(startPos);
//		byte[] offsetBuff = new byte[64];
//		byte[] offsetBytes = Integer.toString(startPos).getBytes();
//		for(int i = 0; i < offsetBytes.length; ++i){
//			offsetBuff[i] = offsetBytes[i];
//		}
//		offsetBuff[offsetBytes.length] = '\0';
//		out.write(offsetBuff, 0, offsetBuff.length);
//		System.out.println("offset: " + offsetBuff);
		
		//writting len
		dataOut.writeInt(writeLen);
//		byte[] lenBuff = new byte[64];
//		byte[] lenBytes = (Integer.toString(writeLen)).getBytes();
//		for(int i = 0; i < lenBytes.length;++i){
//			lenBuff[i] = lenBytes[i];
//        }
//		lenBuff[lenBytes.length] = '\0';
//		out.write(lenBuff, 0, lenBuff.length);
//		System.out.println("writing len: " + lenBuff);
//		out.flush();
		
		//content
		int bufferSize = FileOperation.UPLOAD_BUFFER_SIZE;
		byte[] contentBuff = new byte[bufferSize];
		int contentCount = 0;
		while(contentCount < writeLen){
			int writeNum = Math.min(bufferSize, writeLen-contentCount);
			for(int i = 0; i < bufferSize; ++i){
				contentBuff[i] = buf[contentCount+i];
			}
			out.write(contentBuff, 0, writeNum);
			out.flush();
			
			contentCount += writeNum;
		}
		
		DataInputStream input = new DataInputStream(socket.getInputStream());
		System.out.println("wait response"); 
		String ret = input.readUTF();     
        System.out.println("write chunk response code: " + ret);    
        // 如接收到 "OK" 则断开连接    
        if ("OK".equals(ret)) {    
            System.out.println("client close");    
            try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
        } 
		
		input.close();
		out.close();
		socket.close();
		return contentCount;
	}
	
	private int readChunk(ChunkInfo chunkInfo, int startPos ,byte[] buf, int readLen) throws UnknownHostException, IOException{
		readLen = Math.min(CHUNK_SIZE-startPos, Math.min(buf.length, readLen));
		if(readLen <= 0){
			return 0;
		}
		
		Socket socket = new Socket(chunkInfo.slaveIP, chunkInfo.port);
		OutputStream out = socket.getOutputStream();
		
		// protocol id
		byte[] protocolBuf = new byte[8];
		this.writeInt(out, protocolBuf, VSFProtocols.READ_CHUNK);
		
		DataOutputStream dataOut = new DataOutputStream(out);
		// chunk_id
		dataOut.writeInt(chunkInfo.chunkId);
//		byte[] chunkBuf = new byte[64];
//		this.writeInt(out, chunkBuf, chunkInfo.chunkId);
		// offset
		dataOut.writeInt(startPos);
//		byte[] offsetBuf = new byte[64];
//		this.writeInt(out, offsetBuf, startPos);
		// read len
		dataOut.writeInt(readLen);
//		byte[] lenBuf = new byte[64];
//		this.writeInt(out, lenBuf, readLen);
//		
//		out.flush();
		
		//content from server
		DataInputStream input = new DataInputStream(socket.getInputStream());
		String ret = input.readUTF();     
        System.out.println("socket, port: " + socket.getPort() + "readChunk response code: " + ret);    

        int currBufCount = 0;
        if ("OK".equals(ret)) {    
            // start downloading
        	int totalSize = input.readInt();  
        	
        	byte[] tempBuf = new byte[FileOperation.DOWNLOAD_BUFFER_SIZE];
        	
        	while(true){
        		if(currBufCount >= totalSize){
        			break;
        		}
        		int cRead = Math.min(totalSize - currBufCount, tempBuf.length);
        		int aRead = input.read(tempBuf, 0, cRead);
        		
        		for(int i = 0; i < aRead; ++i){
        			buf[currBufCount+i] = tempBuf[i];
        		}
        		currBufCount += aRead;
        	}
        } else{
        	System.out.println("fail to read chunk "+ chunkInfo.chunkId + " at " + chunkInfo.slaveIP + ":" + chunkInfo.port);
        }
        
		socket.close();
		return currBufCount;
	}
	
	private boolean appendChunk(ChunkInfo chunkInfo, byte[] buf, int nbyte){
		
		return false;
	}
	
	private void writeInt(OutputStream out, byte[] buf , int value) throws IOException{
		byte[] valBytes = Integer.toString(value).getBytes();
		for(int i = 0; i < valBytes.length; ++i){
			buf[i] = valBytes[i];
		}
		buf[valBytes.length] = '\0';
		out.write(buf, 0, buf.length);
	}
	
	private void writeString(OutputStream out, byte[] buf, String str) throws IOException{
		byte[] strBuf = str.getBytes();
		for(int i = 0; i < strBuf.length; ++i){
			buf[i] = strBuf[i];
		}
		buf[strBuf.length] = '\0';
		out.write(buf, 0, buf.length);
	}
	
	private void readBytes(DataInputStream input, byte[] buf, int len) throws IOException{
		int b = 0;
		while(b < len){
			b += input.read(buf, b, len-b);
		}
	}
	
	private String readJSONString(DataInputStream input) throws IOException{
		int objLen = input.readInt();
    	byte[] objBytes = new byte[objLen];
    	this.readBytes(input, objBytes, objLen);
    	
    	return new String(objBytes);
	}
}



