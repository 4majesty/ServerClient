package vfs.struct;

import java.util.LinkedList;
import java.util.List;

public class FileHandle {
	public int handle;
	public int offset;
	public int mode; // 0 for r_only, 1 for w_only, 2 for r&w, 3 for r&append
	public RemoteFileInfo fileInfo;
	
	public List<ChunkInfo> chunkList = new LinkedList<ChunkInfo>();
	
	public ChunkInfo getChunkInfoByIndex(int index){
		for(int i = 0;i < chunkList.size(); ++i){
			if(chunkList.get(i).fileIndex == index){
				return chunkList.get(i);
			}
		}
		
		return null;
	}
}
