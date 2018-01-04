package vfs.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vfs.client.Client;

public class ListFileServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("downloadservlet");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		int cpage = Integer.parseInt(request.getParameter("page"));
		int num = Integer.parseInt(request.getParameter("num"));
		String type = request.getParameter("type");

		if (type.equals("raquo")) {
			if (cpage + 1 > num) {
				cpage = num;
			} else {
				cpage = cpage + 1;
			}
		} else if (type.equals("laquo")) {
			if (cpage - 1 < 1) {
				cpage = 1;
			} else {
				cpage = cpage - 1;
			}
		}
		response.sendRedirect("../index.jsp?page=" + cpage);
	}
}