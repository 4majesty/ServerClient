package vfs.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vfs.client.Client;

public class FileOpServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Client client = new Client("127.0.0.1", 8877);

		System.out.println("loadservlet");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String filePath = request.getParameter("file_path");
		String opType = request.getParameter("operation_type");
		String folderDir = request.getParameter("folderDir");
		if (opType.equals("download")) {
			client.download("/Users/zsy/Documents/workspace/Java/download.txt", filePath);
		} else if (opType.equals("upload")) {
			client.upload(filePath, folderDir);
		}

		response.sendRedirect("../index.jsp?");
	}
}