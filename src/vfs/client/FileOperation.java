package vfs.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import vfs.struct.ChunkInfo;
import vfs.struct.FileHandle;
import vfs.struct.RemoteFileInfo;
import vfs.struct.VSFProtocols;

public class FileOperation {
	static final int CHUNK_SIZE = 64*1024; // *1024;
	static final int UPLOAD_BUFFER_SIZE = 8*1024;
	
	private String masterIP = null;
	private int masterPort = 0;
	
	public FileOperation(String masterIP, int masterPort){
		this.masterIP = masterIP;
		this.masterPort = masterPort;
	}
	
	public boolean mkdir(String dirName){
		
		return false;
	}
	
	public boolean creat(String remotePath){
		
		return false;
	}
	
	public FileHandle open(String remotePath, String mode){
		FileHandle tempHandle = new FileHandle();
		tempHandle.handle = 0;
		tempHandle.offset = 0;
		tempHandle.mode = 2;
		
		RemoteFileInfo fileInfo = new RemoteFileInfo();
		fileInfo.remotePath = remotePath;
		fileInfo.fileName = remotePath;
		fileInfo.fileType = 0;
		tempHandle.fileInfo = fileInfo;
		
		ChunkInfo chunk0 = new ChunkInfo(1001, "127.0.0.1", 8877, 0, -1);
		ChunkInfo chunk1 = new ChunkInfo(1002, "127.0.0.1", 8877, 1, -1);
		ChunkInfo chunk2 = new ChunkInfo(1003, "127.0.0.1", 8877, 2, -1);
		ChunkInfo chunk3 = new ChunkInfo(1003, "127.0.0.1", 8877, 3, -1);
		List<ChunkInfo> chunkList = new LinkedList<ChunkInfo>();
		chunkList.add(chunk0);
		chunkList.add(chunk1);
		chunkList.add(chunk2);
		chunkList.add(chunk3);
		tempHandle.chunkList = chunkList;
		
		return tempHandle;
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
		
		if(handle.getMaxChunkIndex() < chunkNum){
			handle = this.addChunk(handle, chunkNum - handle.getMaxChunkIndex());
		}
		
		byte[] chunkBuf = new byte[CHUNK_SIZE];
		int writeByteCount = 0;
		
		for(int writeChunkCount = 0; writeChunkCount < chunkNum; ++writeChunkCount){
			int writeByteNum = 0;
			ChunkInfo currentChunk = handle.getChunkInfoByIndex(firstChunkIndex + writeChunkCount);
			if(currentChunk == null){
				System.out.println("invalid file handle");
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
		
		return 0;
	}
	
	public FileHandle setFileSize(FileHandle handle , int fileSize){
		
		
		return null;
	}
	
	public FileHandle addChunk(FileHandle handle , int addNum){
		// TODA ask master for new chunk information.
		return handle;
	}
	
	private int writeChunk(ChunkInfo chunkInfo, int startPos, byte[] buf, int writeLen) throws UnknownHostException, IOException{
		writeLen = Math.min(writeLen-startPos, Math.min(buf.length, writeLen));
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
		
		// chunk_id
		byte[] sizeBuff = new byte[64];
		byte[] sizeBytes = (Integer.toString(chunkInfo.chunkId)).getBytes();
		for(int i = 0; i < sizeBytes.length;++i){
			sizeBuff[i] = sizeBytes[i];
        }
		sizeBuff[sizeBytes.length] = '\0';
		out.write(sizeBuff, 0, sizeBuff.length);
		System.out.println("chunk_id: " + sizeBuff);
		
		//offset
		byte[] offsetBuff = new byte[64];
		byte[] offsetBytes = Integer.toString(startPos).getBytes();
		for(int i = 0; i < offsetBytes.length; ++i){
			offsetBuff[i] = offsetBytes[i];
		}
		offsetBuff[offsetBytes.length] = '\0';
		out.write(offsetBuff, 0, offsetBuff.length);
		System.out.println("offset: " + offsetBuff);
		
		//writting len
		byte[] lenBuff = new byte[64];
		byte[] lenBytes = (Integer.toString(writeLen)).getBytes();
		for(int i = 0; i < lenBytes.length;++i){
			lenBuff[i] = lenBytes[i];
        }
		lenBuff[lenBytes.length] = '\0';
		out.write(lenBuff, 0, lenBuff.length);
		System.out.println("writing len: " + lenBuff);
		out.flush();
		
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
		
		out.close();
		socket.close();
		return contentCount;
	}
	
	private boolean readChunk(ChunkInfo chunkInfo, byte[] buf){
		
		return false;
	}
	
	private boolean appendChunk(ChunkInfo chunkInfo, byte[] buf, int nbyte){
		
		return false;
	}
}



