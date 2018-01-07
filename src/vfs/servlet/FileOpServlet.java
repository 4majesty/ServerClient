package vfs.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import vfs.client.Client;
import vfs.client.ClientInterface;


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
		if (opType.equals("download")) {
			ClientInterface.client.download("/Users/zsy/Documents/workspace/Java/download.txt", filePath);
		} else if (opType.equals("upload")) {
			ClientInterface.client.upload(filePath, folderDir);
		} else if (opType.equals("create")) {
			boolean T = true;
			ClientInterface.client.create(filePath, T);
		} else if (opType.equals("getDownloads")) {
			HttpSession session = request.getSession();
			session.setAttribute("downloads", getDownloadClients());
		} else if (opType.equals("getUploads")) {
			HttpSession session = request.getSession();
			session.setAttribute("uploads", getUploadClients());
		} else if (opType.equals("delete")) {
			ClientInterface.client.delete(filePath);
		}
		response.sendRedirect("../index.jsp");

	}
}