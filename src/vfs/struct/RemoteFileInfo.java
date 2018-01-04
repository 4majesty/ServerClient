package vfs.struct;

import org.json.JSONObject;

public class RemoteFileInfo {
	public String fileName;
	public int fileType; // 0 for file, 1 for directory 
	public String remotePath;
	
	public void parseJSON(JSONObject obj){
		this.fileName = obj.getString("fileName");
		this.fileType = obj.getInt("fileType");
		this.remotePath = obj.getString("remotePath");
	}
}
