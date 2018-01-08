package vfs.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import vfs.client.Client;
import vfs.client.ClientInterface;
import vfs.struct.RemoteFileInfo;


public class FileOpServlet extends HttpServlet {

	public Map<String, Client> downloadClients = new HashMap<>();
	public Map<String, Client> uploadClients = new HashMap<>();

	public Map<String, Client> getDownloadClients() {

		return downloadClients;

	}

	public Map<String, Client> getUploadClients() {

		return uploadClients;

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("loadservlet");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String filePath = request.getParameter("file_path");
		String opType = request.getParameter("operation_type");
		String folderDir = request.getParameter("folderDir");
		String selectFile = request.getParameter("selectFile");

		HttpSession session = request.getSession();
		List<RemoteFileInfo> updateFileList = new ArrayList<>();

		if (opType.equals("download")) {
			ClientInterface.client.download("/Users/zsy/Documents/workspace/Java/download.txt", filePath+"/"+folderDir);
		} else if (opType.equals("upload")) {
			ClientInterface.client.upload(selectFile,filePath+"/"+folderDir);
			updateFileList = ClientInterface.client.getRemoteFileInfo(filePath);
			session.setAttribute("fileList", updateFileList);
		} else if (opType.equals("create")) {
			boolean T = true;
			ClientInterface.client.create(filePath+"/"+folderDir, T);
			updateFileList = ClientInterface.client.getRemoteFileInfo(filePath);
			session.setAttribute("fileList", updateFileList);
		} else if (opType.equals("getDownloads")) {
			session.setAttribute("downloads", getDownloadClients());
		} else if (opType.equals("getUploads")) {
			session.setAttribute("uploads", getUploadClients());
		} else if (opType.equals("delete")) {
			ClientInterface.client.delete(filePath+"/"+folderDir);
			updateFileList = ClientInterface.client.getRemoteFileInfo(filePath);
			session.setAttribute("fileList", updateFileList);
		}
		response.sendRedirect("../index.jsp?dir=" + filePath);

	}
}