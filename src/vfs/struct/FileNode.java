package vfs.struct;

import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

// binary tree, left for child and right for brother
public class FileNode {
	public String fileName;
	public boolean isDir;
	public FileNode parent;
	public FileNode child;
	public FileNode brother;
	public HashMap<Integer, ChunkInfo> fileChunkTable = new HashMap<Integer, ChunkInfo>();

	public FileNode() {
	}

	public FileNode(String fileName, boolean isDir) {
		this.fileName = fileName;
		this.isDir = isDir;
	}

	public FileNode findChildWithName(String name) {
		FileNode f = child;
		while (true) {
			if (f.fileName.equals(name)) {
				return f;
			} else {
				if (f.brother != null)
					f = f.brother;
				else
					return null;
			}
		}
	}

	public String getPath() {
		String s = new String();
		FileNode f = parent;
		while (f != null) {
			s += f.fileName + "/";
			f = f.parent;
		}
		return s;
	}

	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("name", fileName);
		obj.put("is_dir", isDir);
		if (isDir && child != null)
			obj.put("child", child.toJSON());
		if (!isDir) {
			JSONArray chunk_ids = new JSONArray();
			for (ChunkInfo chunkInfo : fileChunkTable.values()) {
//				JSONObject chunk = new JSONObject();
//				chunk.put("chunk_id", chunkInfo.chunkId);
//				chunk.put("slave_ip", chunkInfo.slaveIP);
//				chunk.put("port", chunkInfo.port);
//				chunk.put("file_index", chunkInfo.fileIndex);
//				chunk.put("chunk_left", chunkInfo.chunkLeft);
				chunk_ids.put(chunkInfo.chunkId);
			}
			obj.put("chunk_ids", chunk_ids);
		}
		if (brother != null)
			obj.put("brother", brother.toJSON());
		return obj;
	}

	public void parseJSON(JSONObject obj, FileNode parent) throws JSONException {
		fileName = obj.getString("name");
		isDir = obj.getBoolean("is_dir");
		if (parent != null)
			this.parent = parent;
		if (isDir) {
			if (obj.has("child")) {
				child = new FileNode();
				child.parseJSON(obj.getJSONObject("child"), this);
			}
		} else {
			JSONArray chunk_ids = obj.getJSONArray("chunk_ids");
			fileChunkTable = new HashMap<Integer, ChunkInfo>();
			for (int i = 0; i < chunk_ids.length(); i++) {
				ChunkInfo chunkInfo = new ChunkInfo(chunk_ids.getInt(i));
				fileChunkTable.put(chunkInfo.chunkId, chunkInfo);
			}
		}
		if (obj.has("brother")) {
			brother = new FileNode();
			brother.parseJSON(obj.getJSONObject("brother"), parent);
		}
	}
}
