package vfs.struct;

import java.util.HashMap;

// binary tree, left for child and right for brother
public class FileNode {
	public boolean is_Dir;
	public FileNode parrent;
	public FileNode child;
	public FileNode brother;
	public HashMap<Integer, ChunkInfo> fileChunkTable = new HashMap<Integer , ChunkInfo>();
}
