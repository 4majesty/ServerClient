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

		Client client = new Client("127.0.0.1", 8807);

		System.out.println("loadservlet");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String filePath = request.getParameter("file_path");
		String opType = request.getParameter("operation_type");

		if (opType == "download") {
			client.download(filePath, filePath);
		} else if (opType == "upload") {
			client.upload(filePath, filePath);
		}

		response.sendRedirect("../index.jsp?");
	}
}