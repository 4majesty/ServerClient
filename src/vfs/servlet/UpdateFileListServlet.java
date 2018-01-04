package vfs.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import vfs.client.Client;
import vfs.struct.RemoteFileInfo;

public class UpdateFileListServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("allready update~~~");

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String parentFilePath = request.getParameter("parent_file_path");

		System.out.println(parentFilePath);

		Client client = new Client("127.0.0.1", 8807);
		List<RemoteFileInfo> updateFileList = new ArrayList<>();
		// updateFileList = client.getRemoteFileInfo(parentFilePath);

		RemoteFileInfo info1 = new RemoteFileInfo();
		info1.fileName = "subfile";
		info1.fileType = 1;
		info1.remotePath = "workspace/subfile";
		updateFileList.add(info1);

		HttpSession session = request.getSession();

		session.setAttribute("fileList", updateFileList);
		response.sendRedirect("../index.jsp");

	}
}