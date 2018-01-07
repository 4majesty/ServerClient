package vfs.struct;

import java.util.ArrayList;

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
	public ArrayList<Integer> chunkIDList;

	// Note: only use it in parsing JSON
	public FileNode() {
	}

	public FileNode(String fileName, boolean isDir, FileNode parent) {
		this.fileName = fileName;
		this.isDir = isDir;
		this.parent = parent;
		this.chunkIDList = new ArrayList<Integer>();
	}

	public int getChunkSize() {
		return chunkIDList.size();
	}

	public void addChunk(int chunkID) {
		chunkIDList.add(chunkID);
	}

	public void removeChunk(int chunkID) {
		for (int i = 0; i < chunkIDList.size(); i++) {
			if (chunkIDList.get(i) == chunkID)
				chunkIDList.remove(i);
		}
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
			for (Integer chunkID : chunkIDList) {
				chunk_ids.put(chunkID);
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
			chunkIDList = new ArrayList<Integer>();
			for (int i = 0; i < chunk_ids.length(); i++) {
				chunkIDList.add(chunk_ids.getInt(i));
			}
		}
		if (obj.has("brother")) {
			brother = new FileNode();
			brother.parseJSON(obj.getJSONObject("brother"), parent);
		}
	}
}
