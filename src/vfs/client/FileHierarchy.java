package vfs.client;

import java.util.ArrayList;

import org.json.JSONObject;

import vfs.struct.FileNode;

public class FileHierarchy {

	private FileNode root;

	public FileHierarchy() {
		root = new FileNode("vfs", true, null);
	}

	public FileHierarchy(JSONObject json) {
		root = new FileNode();
		root.parseJSON(json, null);
	}

	public JSONObject toJSON() {
		return root.toJSON();
	}

	public boolean mkdir(String path, String dirName) {
		FileNode fileNode = pathToFileNode(path);
		if (fileNode == null || !checkFileName(dirName))
			return false;
		if (!fileNode.isDir)
			return false;
		FileNode parent = fileNode;
		if (fileNode.child == null) {
			parent.child = new FileNode(dirName, true, parent);
			return true;
		}

		fileNode = fileNode.child;
		while (fileNode.brother != null) {
			if (fileNode.fileName.equals(dirName))
				return false;
			fileNode = fileNode.brother;
		}
		fileNode.brother = new FileNode(dirName, true, parent);
		return true;
	}

	public FileNode openFile(String path, String name) {
		FileNode parent = pathToFileNode(path);
		if (parent == null)
			return null;
		FileNode fileNode = parent.findChildWithName(name);
		// if not exist, create a new file
		if (fileNode == null) {
			if (parent.child == null) {
				parent.child = new FileNode(name, false, parent);
				fileNode = parent.child;
			} else {
				fileNode = parent.child;
				while (fileNode.brother != null) {
					fileNode = fileNode.brother;
				}
				fileNode.brother = new FileNode(name, false, parent);
				fileNode = fileNode.brother;
			}
		}
		return fileNode;
	}

	public FileNode OpenDir(String path) {
		return pathToFileNode(path);
	}

	public FileNode remove(String path, String dirName) {
		FileNode fileNode = pathToFileNode(path);
		if (fileNode == null)
			return null;
		FileNode parent = fileNode;
		fileNode = parent.child;
		if (fileNode != null) {
			if (fileNode.fileName.equals(dirName)) {
				parent.child = fileNode.brother;
				fileNode.brother = null;
				return fileNode;
			}
		} else {
			return null;
		}
		FileNode bigBrother;
		do {
			// if (fileNode == null){
			// return null;
			// }
			// if (fileNode.brother == null)
			// return null;
			bigBrother = fileNode;
			fileNode = fileNode.brother;
			if (fileNode == null) {
				return null;
			}
		} while (!fileNode.fileName.equals(dirName));
		bigBrother.brother = fileNode.brother;
		fileNode.brother = null;
		return fileNode;
	}

	private boolean checkFileName(String name) {
		if (name.contains("/") || name.contains("\\\\") || name.contains(":") || name.contains("*")
				|| name.contains("?") || name.contains("\"") || name.contains("<") || name.contains(">")
				|| name.contains("|")) {
			return false;
		} else {
			return true;
		}
	}

	private FileNode pathToFileNode(String path) {
		String[] tempNames = path.split("[/\\\\]");
		FileNode fileNode = root;
		ArrayList<String> names = new ArrayList<String>();
		for (String name : tempNames) {
			if (!name.equals(""))
				names.add(name);
		}
		for (int i = 1; i < names.size(); i++) {
			fileNode = fileNode.findChildWithName(names.get(i));
			if (fileNode == null)
				return null;
		}
		return fileNode;
	}
}
