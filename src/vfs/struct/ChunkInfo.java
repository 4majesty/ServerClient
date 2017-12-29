package vfs.struct;

public class ChunkInfo {
	public int chunkId;
	public String slaveIP;
	public int fileIndex; // index for corresponding file
	public int chunkLeft; // chunk's size is 64MB
}
