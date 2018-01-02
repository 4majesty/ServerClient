package vfs.struct;

public class ChunkInfo {
	public int chunkId;
	public String slaveIP;
	public int port;
	public int fileIndex; // index for corresponding file
	public int chunkLeft; // chunk's size is 64MB. for development, chunk's size is 64k
	
	public ChunkInfo(int chunkId, String slaveIP, int port, int fileIndex, int chunkLeft){
		this.chunkId = chunkId;
		this.slaveIP = slaveIP;
		this.port = port;
		this.fileIndex = fileIndex;
		this.chunkLeft = chunkLeft;
	}
}
