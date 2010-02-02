package com.hunthawk.reader.pps;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.hunthawk.reader.domain.resource.ResourceType;

/**
 * 字符串处理工具类
 * 
 * @author liuxh
 * 
 */
public class StrUtil {

	public static String urlEncode(String content, String encode) {
		try {
			return URLEncoder.encode(content, encode);
		} catch (Exception e) {
			e.printStackTrace();
			return content;
		}
	}

	/**
	 * @param str
	 *            需要显示的字符串
	 * @param len
	 *            需要显示的长度(注意：长度是以byte为单位的，一个汉字是2个byte)
	 * @param symbol
	 *            用于表示省略的信息的字符，如“...”,“>>>”等。 只需要提供一个符号即可 表示占位符
	 * @return 返回处理后的字符串
	 */

	public static String getLimitStr(String str, int bytes, String symbol)
			throws Exception {
		// 1.过滤全角字符
//		str = QBchange(str);
//		System.out.println("2."+str);
		// 2.先判断原始字符串总长度
		byte def_b[] = str.getBytes("GBK");
		if (def_b.length <= bytes) {
			return str;
		}
		// 3.中英文匹配
		Pattern a = Pattern.compile("[\u4e00-\u9fa5]+$");
		String[] split = str.split("");
		int len = 0;
		StringBuilder sb = new StringBuilder();
		for (String s : split) {
			Matcher b = a.matcher(s);
			if (!"".equals(s)) {
				if (b.matches()) {
					// System.out.println(s + " is chinese");
					len += 2;
					if (len <= bytes) {
						if (len <= bytes - 2)
							sb.append(s);
						else {
							for (int i = 0; i < bytes - len; i++)
								sb.append(symbol);
						}
						continue;
					}
				} else {
					// System.out.println(s + " is NOT chinese");
					len += 1;
					if (len <= bytes) {
						if (len <= bytes - 3)
							sb.append(s);
						else {
							for (int i = 0; i < bytes - len; i++)
								sb.append(symbol);
						}
						continue;
					}
				}
			}
			if (len >= bytes) {
				break;
			}
		}
		// System.out.println("len="+len);
		try {
			if (len > bytes) {
				byte b[] = sb.toString().getBytes("GBK");
				// System.out.println("b.lenght="+b.length);
				if (b.length < bytes) {
					for (int i = 0; i < bytes - b.length; i++)
						sb.append(symbol);
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	// 全角转半角
	public static final String QBchange(String QJstr) {
		String outStr = "";
		String Tstr = "";
		byte[] b = null;

		for (int i = 0; i < QJstr.length(); i++) {
			try {
				Tstr = QJstr.substring(i, i + 1);
				b = Tstr.getBytes("unicode");
			} catch (java.io.UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (b[3] == -1) {
				b[2] = (byte) (b[2] + 32);
				b[3] = 0;
				try {
					outStr = outStr + new String(b, "unicode");
				} catch (java.io.UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else
				outStr = outStr + Tstr;
		}
		return outStr;
	}

	public static String toUnicode(String s) {
		StringBuffer sb = new StringBuffer();
		String sGB = null;
		if (s == null || s.equals(""))
			return "";
		for (int i = 0; i < s.length(); i++) {
			char cChar = s.charAt(i);
			// if(isChinese(cChar))
			if (((int) cChar) > 256) {
				if (isChinese2(cChar)) {
					sb.append("&#x");
					sb.append(Integer.toHexString(cChar));
					sb.append(";");
				}
				continue;
			} else if (((int) cChar) > 127) {
				sb.append("&#x");
				sb.append(Integer.toHexString(cChar));
				sb.append(";");
				continue;
			} else if (((int) cChar) < 32) { // 
				continue;
			}
			switch (cChar) {
			case 32: // ' '
				sb.append("&nbsp;");
				break;

			case '$': // '$' 36
				sb.append("＄");
				break;

			case '\'': // '\'' 39
				sb.append("&apos;");
				break;

			case 34: // '"'
				sb.append("&quot;");
				break;

			case 38: // '&'
				sb.append("&amp;");
				break;

			case 60: // '<'
				sb.append("&lt;");
				break;

			case 62: // '>'
				sb.append("&gt;");
				break;
			case 0:
				break;
			case 20: // '\14'
				// sb.append("&gt;");
				break;
			default:
				sb.append(cChar);
				break;
			}
		}

		return sb.toString();
	}

	public static boolean isChinese(char c) {
		Character ch =  Character.valueOf(c);
		String sCh = ch.toString();
		try {
			byte bb[] = sCh.getBytes("gb2312");
			if (bb.length > 1) {
				boolean flag = true;
				return flag;
			}
		} catch (UnsupportedEncodingException ue) {
			ue.printStackTrace();
			boolean flag1 = false;
			return flag1;
		}
		return false;
	}

	static sun.io.CharToByteGBK tool = new sun.io.CharToByteGBK();

	public static boolean isChinese2(char ch) {
		return tool.canConvert(ch);
	}

	/**
	 * 十六进制转字符串。
	 * 
	 * @param str
	 *            源字符串
	 * @return 转换后的字符串
	 */
	public static String hex2Str(String hex) {
		return hex2Str(hex, "UTF-8");

	}

	public static String hex2Str(String hex, String charset) {
		if (null == hex)
			return "";

		String res = "";
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
					16);
		}

		try {
			if ("unicode".equalsIgnoreCase(charset))
				res = new String(bts);
			else
				res = new String(bts, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 字符串转十六进制。
	 * 
	 * @param str
	 *            源字符串
	 * @return 转换后的字符串
	 */
	public static String str2Hex(String source) {
		return str2Hex(source, "UTF-8");
	}

	public static String str2Hex(String source, String charset) {
		StringBuffer strBuff = new StringBuffer(100);
		String stmp = null;
		if (source == null) {
			return null;
		}

		try {
			byte[] strByte = source.getBytes(charset);
			for (int i = 0; i < strByte.length; i++) {
				stmp = Integer.toHexString(strByte[i] & 0xFF);
				if (stmp.length() == 1) {
					strBuff.append("0");
				}
				strBuff.append(stmp);
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		return strBuff.toString().trim().toUpperCase();
	}
	/**
	 * 为Velocity使用，Velocity本身不支持数组方法
	 * @param objs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List getArraySize(Object[] objs){
		List list = new ArrayList();
		if(objs != null){
			for(Object obj : objs){
				list.add(obj);
			}
		}
		return list;
	}
	/**
	 * 根据资源ID获取资源类型
	 * @param resourceId
	 * @return
	 * @author liuxh 09-11-10
	 */
	public static String getResourceType(String resourceId){
		if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_BOOK))){
			return "书";
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_COMICS))){
			return "漫";
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_MAGAZINE))){
			return "杂";
		}else if(resourceId.startsWith(String.valueOf(ResourceType.TYPE_NEWSPAPERS))){
			return "报";
		}
		return "";
	}

	/**
	 * 拆分资源名称 如：他生活(2009-11-11期)
	 * @param flag 标识 0.名称 1.期刊数
	 * @param name 资源名称
	 * @return  默认返回名称
	 * @author liuxh 09-11-11
	 */
	public static String splitName(String name,int flag){
		if(StringUtils.isEmpty(name))
			return name;
		int splitIndex=name.lastIndexOf("(");
		int splitEnd=name.lastIndexOf(")");
		if(splitIndex<0 || splitEnd<0 || splitEnd<splitIndex)
			return name;
		String issn=name.substring(splitIndex+1,splitEnd);//期刊数
		String rName=name.substring(0,splitIndex);
		if(flag==1)
			return StringUtils.trim(issn);
		else if(flag==0)
			return StringUtils.trim(rName);
		return name;
	}
	public static void main(String[] args) {
		// String patternString = "黑客帝国123";
		// String patternString = "黑客帝国3中国123";
		// String patternString="adf黑客3帝国中国123";
//		String patternString = "我吃西红柿";
//		try {
//			System.out.println(getLimitStr(patternString, 12, "."));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String name="他生活   (2009-11-11期)";
		System.out.println(splitName(name,0));
		System.out.println(splitName(name,1));
	}
}
