<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="vfs.servlet.FileOpServlet"%>
<%@page import="vfs.servlet.UpdateFileListServlet"%>
<%@page import="vfs.servlet.ListFileServlet"%>
<%@page import="vfs.client.Client"%>
<%@page import="vfs.struct.RemoteFileInfo"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
	Client client = new Client("127.0.0.1", 8877);

	List<RemoteFileInfo> fileList = new ArrayList<RemoteFileInfo>();
	if (session.getAttribute("fileList") != null) {
		fileList = (List<RemoteFileInfo>) session.getAttribute("fileList");
	} else {
		fileList = client.getRemoteFileInfo("vfs");
	}
	/* RemoteFileInfo info1 = new RemoteFileInfo();
	info1.fileName = "file1";
	info1.fileType = 1;
	info1.remotePath = "workspace/Client/data/file1";
	fileList.add(info1);
	RemoteFileInfo info2 = new RemoteFileInfo();
	info2.fileName = "file2";
	info2.fileType = 0;
	info2.remotePath = "workspace/file2";
	fileList.add(info2);
	RemoteFileInfo info3 = new RemoteFileInfo();
	info3.fileName = "file3";
	info3.fileType = 0;
	info3.remotePath = "workspace/file3";
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3);
	fileList.add(info3); */

	String folderDir;
	if (!fileList.isEmpty()) {
		String fileRemotePath[] = fileList.get(0).remotePath.split("/");
		String tmpPath = "";
		if (fileRemotePath.length > 1) {
			for (int i = 0; i < fileRemotePath.length - 1; i++) {
				tmpPath = tmpPath + "/" + fileRemotePath[i];
			}
			folderDir = tmpPath;
		} else {
			folderDir = fileRemotePath[0];
		}
	} else {
		if (request.getAttribute("dir") != null) {
			folderDir = request.getAttribute("dir").toString();
		} else {
			folderDir = "vfs";
		}
	}
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	request.setAttribute("path", basePath);
	int cpage;
	if (request.getParameter("page") == null) {
		cpage = 1;
	} else {
		cpage = Integer.parseInt(request.getParameter("page"));
	}
	int filesPerPage = 5;
	int totalPages;
	int rem = fileList.size() % filesPerPage;

	if (rem == 0) {
		totalPages = fileList.size() / filesPerPage;
	} else {
		totalPages = (fileList.size() + filesPerPage - rem) / filesPerPage;
	}
	int beginIndex = (cpage - 1) * (int) filesPerPage;
	// 本页末尾用户序号的下一个
	int endIndex = beginIndex + filesPerPage;
	if (endIndex > fileList.size())
		endIndex = fileList.size();
%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width charset=UTF-8" />
<title>VFS</title>
<script src="${path}js/jquery.min.js"></script>
<link rel="stylesheet" href="${path}css/bootstrap.min.css">
<script src="${path}js/bootstrap.min.js"></script>
<script src="${path}js/bootstrap-table.js"></script>
<link href="${path}css/bootstrap-table.css" rel="stylesheet" />
<script type="text/javascript">
	function getInputURL() {
		alert('uploading ...');
 		//var myselect = document.getElementById("fileInput");
 		var select = $("#fileInput").val();
		var selectPath = select.split("\\");
		var localFileRoot  = '/Users/zsy/Documents/workspace/Java/';
		
		var selectName = selectPath[selectPath.length-1];
		var select = localFileRoot + selectName;
		
		var opType = "upload";
		var folderName = "<%=folderDir%>";
		window.location.href = "${path}servlet/FileOpServlet?file_path="+select+"&folderDir="+folderName+"/"+selectName+"&operation_type="+opType;
	}
</script>
<script type="text/javascript">
	function downloadFile(num) {
		var downloadPath = $("#"+num).text();
		var folderName = "<%=folderDir%>";
		var opType = "download";
		window.location.href = "${path}servlet/FileOpServlet?file_path="+folderName+"/"+downloadPath+"&operation_type="+opType;
	}
</script>
<script type="text/javascript">
	function getSubFiles() {
		var remotePath = document.getElementById("openFileFolder");
		window.location.href = "${path}servlet/UpdateFileListServlet?parent_file_path="+remotePath+"isReturn=0";
	}
</script>
<script type="text/javascript">
	function returnParent(parentRemotePath) {
		window.location.href = "${path}servlet/UpdateFileListServlet?parent_file_path="+parentRemotePath+"";
	}
</script>
</head>
<body>
	<div id="toolbar" class="btn-group">
		<button id="btn_add" type="button" class="btn btn-primary btn-lg"
			data-toggle="modal" data-target="#uploadModel">
			<span class="glyphicon glyphicon-cloud-upload" aria-hidden="true"></span>上传进度
		</button>
		<button id="btn_edit" type="button" class="btn btn-primary btn-lg"
			data-toggle="modal" data-target="#downloadModel">
			<span class="glyphicon glyphicon-cloud-download" aria-hidden="true"></span>下载进度
		</button>
	</div>
	<div class="modal fade" id="uploadModel" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">正在上传...</h4>
				</div>
				<div class="modal-body">
					<table
						class="table table-hover table-responsive table-striped table-bordered">
						<thead>
							<tr>
								<th>文件名</th>
								<th>文件类型</th>
								<th>下载</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td></td>
								<td></td>
								<td></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<!-- /.modal -->
	</div>
	<div class="modal fade" id="downloadModel" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">正在下载...</h4>
				</div>
				<div class="modal-body">
					<table
						class="table table-hover table-responsive table-striped table-bordered">
						<thead>
							<tr>
								<th>文件名</th>
								<th>文件类型</th>
								<th>下载</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td></td>
								<td></td>
								<td></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<!-- /.modal -->
	</div>
	<div class="panel-body" style="padding-bottom: 0px;">
		<div class="panel panel-default">
			<div class="panel-heading">
				上传到当前目录:
				<%=folderDir%></div>
			<div class="panel-body">
				<form id="formSearch" class="form-horizontal">
					<div class="form-group" style="margin-top: 15px">
						<label class="control-label col-sm-1"
							for="txt_search_departmentname">选择</label>
						<div class="col-sm-3">
							<input type="file" class="form-control" id="fileInput">
						</div>
						<div class="col-sm-4" style="text-align: left;">
							<button type="button" style="margin-left: 50px" id="btn_query"
								class="btn btn-primary" onclick="getInputURL()">
								<span class="glyphicon glyphicon-cloud-upload"
									aria-hidden="true"></span>
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<table
		class="table table-hover table-responsive table-striped table-bordered">
		<caption>文件信息</caption>
		<button class="btn-download" style="width: 150px;"
			onclick="returnParent()">返回上级目录</button>
		<thead>
			<tr>
				<th>文件名</th>
				<th>文件类型</th>
				<th>下载</th>
			</tr>
		</thead>
		<tbody>
			<%
				int fileNum;
				for (fileNum = beginIndex; fileNum < endIndex; fileNum++) {
			%><tr>
				<%
					if (fileList.get(fileNum).fileType == 0) {
				%>
				<td id=<%=fileNum%>><%=fileList.get(fileNum).fileName%></td>
				<td><span class="glyphicon glyphicon-file" aria-hidden="true"></span></td>
				<%
					} else if (fileList.get(fileNum).fileType == 1) {
				%>
				<td id=<%=fileNum%>><a id="openFileFolder"
					href="javascript:void(0);" onclick="getSubFiles()"><%=fileList.get(fileNum).fileName%></a></td>

				<td><span class="glyphicon glyphicon-folder-close"
					aria-hidden="true"></span></td>

				<%
					}
				%>
				<td><button class="btn-download" style="width: 81px;"
						onclick="downloadFile(<%=fileNum%>)">
						<span class="glyphicon glyphicon-save" aria-hidden="true"></span>
					</button></td>
			</tr>
			<%
				}
			%>

		</tbody>
	</table>
	<div class="text-center">
		<nav>
			<ul class="pagination">
				<li><a href="index.jsp?page=1">首页</a></li>
				<li><a
					href="${path}servlet/ListFileServlet?page=<%=cpage%>&type=laquo&num=<%=totalPages%>">&laquo;</a></li>
				<c:forEach begin="1" end="<%=totalPages%>" varStatus="loop">
					<c:set var="active" value="${loop.index==cpage?'active':''}" />
					<li class="${active}"><a
						href="${path}index.jsp?page=${loop.index}">${loop.index}</a></li>
				</c:forEach>
				<li><a
					href="${path}servlet/ListFileServlet?page=<%=cpage%>&type=raquo&num=<%=totalPages%>">&raquo;</a>
				</li>
				<li><a href="index.jsp?page=<%=totalPages%>">尾页</a></li>
			</ul>
		</nav>
	</div>
</body>
</html>