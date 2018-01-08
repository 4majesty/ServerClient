package vfs.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import vfs.client.ClientInterface;
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
		String folderDir = "";

		System.out.println(parentFilePath);
		String isReturn = request.getParameter("isReturn");

		if (isReturn.equals("1") && !parentFilePath.equals("vfs")) {
			String fileRemotePath[] = parentFilePath.split("/");
			String tmpPath = "";
			if (fileRemotePath.length > 1) {
				for (int i = 0; i < fileRemotePath.length - 1; i++) {
					if (i == fileRemotePath.length - 2) {
						tmpPath = tmpPath + fileRemotePath[i];
					} else {
						tmpPath = tmpPath + fileRemotePath[i] + "/";
					}
				}
				folderDir = tmpPath;
			} else {
				folderDir = fileRemotePath[0];
			}
			parentFilePath = folderDir;
		}
		List<RemoteFileInfo> updateFileList = new ArrayList<>();
		updateFileList = ClientInterface.client.getRemoteFileInfo(parentFilePath);

		// RemoteFileInfo info1 = new RemoteFileInfo();
		// info1.fileName = "subfile";
		// info1.fileType = 1;
		// info1.remotePath = "workspace/Client/data/file1/subfile";
		// updateFileList.add(info1);

		HttpSession session = request.getSession();

		session.setAttribute("fileList", updateFileList);
		response.sendRedirect("../index.jsp?dir=" + parentFilePath);

	}
}
