package com.hunthawk.reader.timer.util;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * �Զ����ҳ��ǩ 1.�ӱ��л��ȫ����ѯ��������,���ɶ�Ӧ��<input type=hidden>�Ա��´��ύ��ѯ����������ֵ.
 * 2.�ѵ�ǰҳ��(pageNo)���ó����������,�Ա��̨������Ի�ø�ֵ����ʱʱ���������ݿ��ѯ��ӦҪ��ʾ������ 3.ʹ�þ���: a) ��̨����:
 * //����ÿҳҪ��ʾ�ļ�¼���������浽���������(���벽��) int pageSize = 20;
 * request.setAttribute("pageSize", pageSize); //��ȡ��ǰҳ��(���벽��) String pageNo =
 * request.getParameter("pageNo"); //�������������������ݲ�ѯ ... // ֧�ַ�ҳ�������������õ��ܼ�¼��(���벽��)
 * request.setAttribute("total", size + "");
 * 
 * 
 * b) ҳ�����ȵ����ǩ��: <%@ taglib uri="http://www.hunthawk.cn" prefix="page"%> 
 * c) ҳ��ʹ�ñ�ǩ:
 * <q:pageTag recordCount="ÿҳ��ʾ�ļ�¼��" gotoURI="Ҫ��ת����Ŀ�ĵ�"/>
 * 
 * @author penglei
 */
public class PageControllerTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -682964164914291285L;

	/** ÿҳ��ʾ�ļ�¼��(��ǩ������) */
	private int pageSize = 20;

	/** Ŀ�ĵ�(��ǩ������) */
	private String gotoURI;

	/** �ܼ�¼���� */
	public static final String TOTAL = "total";

	/** ��ǰҳ���� */
	public static final String PAGNENO = "pageNo";

	/** ÿҳҪ��ʾ�ļ�¼���� */
	public static final String RECORDCOUNT = "pageSize";

	/** Ŀ�ĵ��� */
	public static final String GOTOURI = "gotoURI";

	// ��ǩ�������
	public int doStartTag() throws JspException {
		/** ��ǰҳ��(����������еõ�) */
		int pageNo = 1;
		/** �ܼ�¼��(����������еõ�) */
		int total = 0;
		/** ��ҳ��(����ó�) */
		int totalPage = 1;

		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();

		// Ҫ�����ҳ���HTML�ı�
		StringBuffer sb = new StringBuffer();

		sb.append("\r\n<form method='post' action='' ").append(
				"name='pageController'>\r\n");

		// ��ȡ�����ύ�Ĳ���(������ѯ��������)
		Enumeration enumeration = request.getParameterNames();
		String name = null;
		String value = null;
		while (enumeration.hasMoreElements()) {
			name = (String) enumeration.nextElement();
			value = request.getParameter(name);
			if (name.equals(RECORDCOUNT)) {
				continue;
			}
			// ����������л�ȡҪ��ת����ҳ��
			if (name.equals(PAGNENO)) {
				if (null != value && !"".equals(value)) {
					pageNo = Integer.parseInt(value);
					// System.out.println("from request pageNo====>" + pageNo);
				}
				continue;
			}

			sb.append("<input type='hidden' name='").append(name).append(
					"' value='").append(value).append("'/>\r\n");
		}

		// �ѵ�ǰҳ�����ó��������
		// System.out.println("tag:pageNo=====>" + pageNo);
		sb.append("<input type='hidden' name='").append(PAGNENO).append(
				"' value='").append(pageNo).append("'/>\r\n");

		// ����������л�ȡ�ܼ�¼��
		Long tot = (Long) request.getAttribute(TOTAL);
		if (null != tot) {
			total = tot.intValue();
		}
		// ������ҳ��
		totalPage = getTotalPage(total);

		// System.out.println("total-->" + total);

		sb.append("<hr width='97%'/>\r\n");
		sb
				.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
		sb.append("�� ").append(totalPage).append(" ҳ,��ǰ�� ").append(pageNo)
				.append(" ҳ\r\n");
		sb
				.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");

		sb
				.append("<input type='button' value='��ҳ' onClick='turnOverPage(1)'>\r\n");
		sb.append("&nbsp;");
		sb.append("<input type='button' value='��һҳ' onClick='turnOverPage(")
				.append("" + (pageNo - 1)).append(")'>\r\n");

		sb.append("&nbsp;");

		sb.append("<input type='button' value='��һҳ' onClick='turnOverPage(")
				.append("" + (pageNo + 1)).append(")'>\r\n");

		sb.append("&nbsp;");
		sb.append("<input type='button' value='ĩҳ' onClick='turnOverPage(")
				.append("" + totalPage).append(")'>\r\n");

		sb.append("&nbsp;");

		sb.append("����<select onChange='turnOverPage(this.value)'>\r\n");

		for (int i = 1; i <= totalPage; i++) {
			if (i == pageNo) {
				sb.append(" <option value='").append(i).append("' selected>��")
						.append(i).append("ҳ</option>\r\n");
			} else {
				sb.append(" <option value='").append(i).append("'>��").append(i)
						.append("ҳ</option>\r\n");
			}
		}
		sb.append("</select>\r\n");

		// sb.append("<input type='text' name='").append(PAGNENO).append(
		// "' size='3' maxlength='3'/>\r\n");
		// sb.append("<input type='button' value='GO'").append(
		// "onclick='turnOverPage(pageNo.value)'/>\r\n");
		sb.append("&nbsp;\r\n");
		sb.append("</form>\r\n");

		// �����ύ����JS
		sb.append("<script language='javascript'>\r\n");
		sb.append(" function turnOverPage(no){\r\n");
		sb.append("    var form = document.pageController;\r\n");
		sb.append("    //ҳ��Խ�紦��\r\n");
		sb.append("    if(no").append(">").append(totalPage).append(") {\r\n");
		sb.append("        no=").append(totalPage).append(";\r\n");
		sb.append("    }\r\n");
		sb.append("    if(no").append("<=0){\r\n");
		sb.append("        no=1;\r\n");
		sb.append("    }\r\n");
		sb.append("    form.").append(PAGNENO).append(".value=no;\r\n");
		sb.append("    form.action='").append(gotoURI);
		if(gotoURI.indexOf("?")<0){
			sb.append("?pageNo=").append("'+no;\r\n");
		}else{
			sb.append("&pageNo=").append("'+no;\r\n");
		}
		sb.append("    form.submit();\r\n");
		sb.append(" }\r\n");
		sb.append("</script>\r\n");

		try {
			pageContext.getOut().println(sb.toString());
		} catch (IOException e) {

			e.printStackTrace();
		}
		return super.doStartTag();
	}

	public String getGotoURI() {
		return gotoURI;
	}

	public void setGotoURI(String gotoURI) {
		this.gotoURI = gotoURI;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * �����ܼ�¼���õ���ҳ��
	 * 
	 * @return int ��ҳ��
	 */
	private int getTotalPage(int total) {
		int totalPage = 1;
		if (total == 0) {
			totalPage = 1;
		} else {
			totalPage = (total % pageSize == 0) ? (total / pageSize) : (total
					/ pageSize + 1);
		}

		return totalPage;
	}

}
