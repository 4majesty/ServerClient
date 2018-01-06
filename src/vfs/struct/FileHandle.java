package vfs.struct;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

// TODO add lock for writing offset
public class FileHandle {
	public int handle;
	public int offset;
	public int mode; // 0 for r_only, 1 for w_only, 2 for r&w, 3 for r&append
	public RemoteFileInfo fileInfo;
	
	public FileHandle(int mode){
		this.offset = 0;
		this.mode = mode;
		fileInfo = new RemoteFileInfo();
	}
	
	public List<ChunkInfo> chunkList = new LinkedList<ChunkInfo>();
	
	public ChunkInfo getChunkInfoByIndex(int index){
		for(int i = 0;i < chunkList.size(); ++i){
			if(chunkList.get(i).fileIndex == index){
				return chunkList.get(i);
			}
		}
		
		return null;
	}
	
	public int getMaxChunkIndex(){
		int maxIndex = -1;
		for(int i = 0;i < chunkList.size(); ++i){
			if(maxIndex < chunkList.get(i).fileIndex){
				maxIndex = chunkList.get(i).fileIndex;
			}
		}
		
		return maxIndex;
	}
	
	public void parseJSON(JSONObject obj){
		this.handle = obj.getInt("handle");
		this.offset = obj.getInt("offset");
		
		JSONObject fileInfoObj = obj.getJSONObject("fileInfo");
		this.fileInfo.parseJSON(fileInfoObj);
		
		this.chunkList = new LinkedList<ChunkInfo>();
		JSONArray chunkArry = obj.getJSONArray("chunkList");
		for(int i = 0; i < chunkArry.length(); ++i){
			ChunkInfo currChunk = new ChunkInfo();
			currChunk.parseJSON(chunkArry.getJSONObject(i));
			this.chunkList.add(currChunk);
		}
	}
}
