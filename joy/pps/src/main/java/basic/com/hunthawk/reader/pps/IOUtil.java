package com.hunthawk.reader.pps;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.htmlparser.filters.*;
import org.htmlparser.*;
import org.htmlparser.nodes.*;
import org.htmlparser.tags.*;
import org.htmlparser.util.*;
import org.htmlparser.visitors.*;
/**
 * IO工具类
 * @author liuxh
 *
 */
public class IOUtil {
	private static final String encoding="GB2312";
    public static String htmlParser(String path,String replaceStr)throws Exception{
    	//String path="http://book.moluck.com/pps/unsubscribe.htm";
        URL url = new URL(path);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true); 
        
        InputStream inputStream = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream, encoding);
        StringBuffer sb = new StringBuffer();
        BufferedReader in = new BufferedReader(isr);
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
            sb.append("\n");
        }
        
        String result = sb.toString();
        result=result.replaceAll("!content!", replaceStr);
        //System.out.println(result);
        in.close();//关闭流
        return result;
    }
	 /**
     * 按页面方式处理.解析标准的html页面
     * @param content 网页的内容
     * @throws Exception
     */
    public static void readByHtml(String content) throws Exception {
        Parser myParser;
        myParser = Parser.createParser(content, encoding);
        HtmlPage visitor = new HtmlPage(myParser);
        myParser.visitAllNodesWith(visitor);

        String textInPage = visitor.getTitle();
        System.out.println(textInPage);
        NodeList nodelist;
        nodelist = visitor.getBody();
        
        System.out.print(nodelist.asString().trim());
    }

    /** *//**
     * 分别读纯文本和链接.
     * @param result 网页的内容
     * @throws Exception
     */
    public static void readTextAndLinkAndTitle(String result) throws Exception {
        Parser parser;
        NodeList nodelist;
        parser = Parser.createParser(result, encoding);
        NodeFilter textFilter = new NodeClassFilter(TextNode.class);
        NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);
        NodeFilter titleFilter = new NodeClassFilter(TitleTag.class);
        OrFilter lastFilter = new OrFilter();
        lastFilter.setPredicates(new NodeFilter[] { textFilter, linkFilter, titleFilter });
        nodelist = parser.parse(lastFilter);
        Node[] nodes = nodelist.toNodeArray();
        String line = "";
        
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            if (node instanceof TextNode) {
                TextNode textnode = (TextNode) node;
                line = textnode.getText();
            } else if (node instanceof LinkTag) {
                LinkTag link = (LinkTag) node;
                line = link.getLink();
            } else if (node instanceof TitleTag) {
                TitleTag titlenode = (TitleTag) node;
                line = titlenode.getTitle();
            }
            
            if (isTrimEmpty(line))
                continue;
            System.out.println(line);
        }
    }
    
    /** *//**
     * 去掉左右空格后字符串是否为空
     */
    public static boolean isTrimEmpty(String astr) {
        if ((null == astr) || (astr.length() == 0)) {
            return true;
        }
        if (isBlank(astr.trim())) {
            return true;
        }
        return false;
    }

    /** *//**
     * 字符串是否为空:null或者长度为0.
     */
    public static boolean isBlank(String astr) {
        if ((null == astr) || (astr.length() == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String args[]) throws Exception {
        //String path = "http://www.blogjava.net/amigoxie";
    	String path="http://book.moluck.com/pps/unsubscribe.htm";
        URL url = new URL(path);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true); 
        
        InputStream inputStream = conn.getInputStream();
        //InputStreamReader isr = new InputStreamReader(inputStream, encoding);
        InputStreamReader isr = new InputStreamReader(inputStream, encoding);
        StringBuffer sb = new StringBuffer();
        BufferedReader in = new BufferedReader(isr);
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
            sb.append("\n");
        }
        
        String result = sb.toString();
        result=result.replaceAll("!content!", "跳转参数不全,请求失败!");
        System.out.println(result);
       // readByHtml(result);
       // readTextAndLinkAndTitle(result);
    }
}
