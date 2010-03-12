/**
 * 
 */
package com.hunthawk.reader.page.guide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.tapestry.engine.IEngineService;

import com.hunthawk.reader.domain.bussiness.SysTag;
import com.hunthawk.reader.domain.bussiness.UserDefTag;
import com.hunthawk.reader.page.util.PageHelper;
import com.hunthawk.reader.service.bussiness.TemplateService;



/**
 * @author sunquanzhi
 *
 */
public class ParseTemplate {
	public static String parse(String template,TemplateService ts,IEngineService service)
	{
		return parse( template, ts,service,true);
	}

	public static String parse(String template,TemplateService ts,IEngineService service,boolean bEscape)
	{
		if(template == null)
		{
			return "";
		}
		PatternCompiler compiler = new Perl5Compiler();
		StringBuffer buffer = new StringBuffer();
		int index = 0;
		try{
			
			Pattern pattern = compiler.compile("\\"+TagConstants.PRE_TAG_SUFFIX+"[^\\"+TagConstants.END_TAG_SUFFIX+"]*\\"+TagConstants.END_TAG_SUFFIX);
			Pattern tagpattern = compiler.compile("\\"+TagConstants.PRE_TAG_SUFFIX+"([^\\"+TagConstants.TAG_PARAMETERS+"]*)\\"+TagConstants.TAG_PARAMETERS+"?(.*)"+TagConstants.END_TAG_SUFFIX);
			PatternMatcher matcher = new Perl5Matcher();
			PatternMatcherInput input = new PatternMatcherInput(template);
			Map<String,String> resultMap = new HashMap<String,String>();
			Map<String,Integer> numMap = new HashMap<String,Integer>();
		    while(matcher.contains(input,pattern))
			{
				
				MatchResult result = matcher.getMatch();
				String str = template.substring(index,result.beginOffset(0));
				if(bEscape)
				{
					//str = HtmlUtil.escapeHTMLTag(str);
				}
				buffer.append(str);
				index = result.endOffset(0);
				String tag = result.group(0);
				PatternMatcher tagmatcher = new Perl5Matcher();
				if(tagmatcher.contains(tag,tagpattern))
				{
						MatchResult rt = tagmatcher.getMatch();
						int count = rt.groups();
						if(count >= 1)
						{
							String tagName = rt.group(1);
							String strParameter = "";
							if(count >=2)
							{
								strParameter = rt.group(2);
							}
							Integer num = numMap.get(tag);
							if(num == null)
							{
								num = new Integer(1);
							}else{
								num ++;
							}
							numMap.put(tag,num);
							
							if(tagName.equals(TagGuide.UserDefTagName))
							{
								try{
									UserDefTag ut = ts.getUserDefTag(Integer.parseInt(strParameter));
									if(ut != null)
									{
										resultMap.put(tag,"{标签[:"+ut.getTitle()+"]}");
									}else{
										String tstr = "{标签[:"+tag+"]}";
										if(bEscape)
										{
											tstr = "<font color=\"red\">"+tstr+"</font>";
										}
										resultMap.put(tag,tstr);
									}
								}catch(Exception e){
									
								}
							}else{
								SysTag sysTag = ts.getSysTagbyName(tagName);
								if(sysTag == null)
								{
									String tstr = "{标签[:"+tag+"]}";
									if(bEscape)
									{
										tstr = "<font color=\"red\">"+tstr+"</font>";
									}
									resultMap.put(tag,tstr);
								}else{
									String tagContent = "{标签:["+sysTag.getTitle()+"]";
									String url = "";
									/*if(!strParameter.equals(""))
									{
										String para = parseParameter(sysTag,strParameter,ms,bs,rs);
										tagContent += "参数:["+para+"]";
									}*/
									if(sysTag.getType().equals(TagGuide.Dialog))
									{
										Object[] params = new Object[5];
										params[0] = sysTag.getWizardParas();
										params[1] = sysTag.getName();
										params[2] = System.currentTimeMillis();
										params[3] = strParameter;
										params[4] = num;
										url = PageHelper.getExternalFunction(service,
												sysTag.getWizardPage(),params );
									}
									tagContent += "}";
									if(!"".equals(url)&&bEscape)
									{
										tagContent = "<input type=\"button\" value=\""+tagContent+"\" class=\"buttonLabel\" onClick=\""+url+"\" />"; 
									}
									
									resultMap.put(tag,tagContent);
								}
							}
							
								
								
							replaceTag(buffer,tag,resultMap);
							
						}
				}				
			
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(index < template.length())
		{
			String str = (template.substring(index,template.length()));
			if(bEscape)
			{
				//str = HtmlUtil.escapeHTMLTag(str);
			}
			buffer.append(str);
		}
		return buffer.toString();
		
	}
	private static Map<String,GuideParameter> parseGuideParaValues(SysTag tag)
	{
		String[] wparas = tag.getWizardParas().split(";");
							  
		Map<String,GuideParameter> result = new HashMap<String,GuideParameter>();
		for(String para:wparas){
			String[] strs = para.split(":");
			if(strs.length < 3)
			{
				continue;
			}
			GuideParameter gp = new GuideParameter();
			gp.setType(strs[0]);
			String[] kv = strs[1].split("=");
			gp.setKey(kv[0]);
			if(kv.length == 2)
			{
				gp.setTitle(kv[1]);
			}else{
				gp.setTitle(kv[0]);
			}
			
			gp.setValue(strs[2]);
			result.put(gp.getKey(),gp);
		}
		return result;
	}
	private static String parseParameter(SysTag tag,String parameter)
	{
		String[] paras = parameter.split(",");
		Map<String,GuideParameter> gps = parseGuideParaValues(tag);
		String paraContent = "";
		for(int i=0;i<paras.length;i++)
		{
			String para = paras[i];
			String[] kv = para.split("=");
			if(kv.length == 2)
			{
				GuideParameter gp = gps.get(kv[0]);
				if(gp != null)
				{
					if("$S".equals(gp.getType()))
					{
						Map map = parseSelect(gp.getValue());
						String value = (String)map.get(kv[1]);
						paraContent += gp.getTitle() + ".";
						if(value == null)
						{
							paraContent += kv[1] + ";";
						}else{
							paraContent += value+";";
						}
					}else if("$T".equals(gp.getType()))
					{
						paraContent += gp.getTitle() + "." + kv[1]+";";
					}
				}else{
					paraContent += para+";";
				}
			}else{
				paraContent += para+";";
			}
		}
		
		return paraContent;
	}
	private static boolean replaceTag(StringBuffer buffer,String tag,Map map)
	{
		String value = (String)map.get(tag);
		if(value != null && !"".equals(value))
		{
			buffer.append((value));
			return true;
		}else{
			return false;
		}
	}
	private static Map parseSelect(String value)
	{
		value = value.substring(1,value.length()-1);
		Map map = new HashMap();
		String[] strs = value.split(",");
		for(int i=0;i<strs.length;i++)
		{
			String[] kv = strs[i].split("=");
		
			map.put(kv[0],kv[1]);
		} 
		return map;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}

}
