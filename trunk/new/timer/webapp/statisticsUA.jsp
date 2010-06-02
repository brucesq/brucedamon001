<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.hunthawk.cn" prefix="page"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>UA List</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<script type="text/javascript" src="effect.js"></script>
		<link rel="stylesheet" type="text/css" href="effect.css" />
		<link rel="stylesheet" type="text/css" href="css.css">

		<script type="text/javascript">
			hs.graphicsDir = './images/';
			hs.outlineType = 'rounded-white';
			hs.wrapperClassName = 'draggable-header';
		</script>
		

	</head>

	<body>
		<div style="overflow:auto; height:430px;">
		<table id="box1" width="100%" border="1" cellpadding="0"
			cellspacing="0" bordercolor="#d5d9dc"
			style="border-collapse: collapse;" >
			<tr>
				<td colspan="3" align="center" bgcolor="#DFDFDF">
					<b>统计未适配的UA列表</b>
				</td>
			</tr>
			<tr align="center" bgcolor="#d5d9dc">
				<td width="80">
					<b>序号</b>
				</td>
				<td>
					<b>UA</b>
				</td>
				<td>
					<B>统计总量</B>
				</td>
			</tr>
			<c:forEach var="item" items="${result}" varStatus="i">
				<c:if test="${i.count%2==0}">
					<tr align="center" class="even">
						<td width="80">
							${i.count }
						</td>
						<c:forEach var="obj" items="${item}" varStatus="status">
							<td>
								<c:if test="${status.count==1}">
									<a href="./ShowUaDetailList?shortUA=${obj }" onclick="return hs.htmlExpand(this, { objectType: 'iframe' } )">${obj }</a>
								</c:if>
								<c:if test="${status.count!=1}">
									${obj }
								</c:if>
							</td>
						</c:forEach>
					</tr>
				</c:if>

				<c:if test="${i.count%2==1}">
					<tr align="center" class="odd">
						<td>
							${i.count }
						</td>
						<c:forEach var="obj" items="${item}" varStatus="status">
							<td>
								<c:if test="${status.count==1}">
									<a href="./ShowUaDetailList?shortUA=${obj }" onclick="return hs.htmlExpand(this, { objectType: 'iframe' } )">${obj }</a>
								</c:if>
								<c:if test="${status.count!=1}">
									${obj }
								</c:if>
							</td>
						</c:forEach>
					</tr>
				</c:if>


			</c:forEach>
		</table>
		</div>

		<page:pageTag pageSize="${pageSize}" gotoURI="./showUA"></page:pageTag>
		
	</body>
</html>
