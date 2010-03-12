/**
 * 
 */
package com.hunthawk.reader.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author BruceSun
 *
 */
public class TestMsgRecord {

	static HashMap map = new HashMap();
	static{
		map.put("中山","760");
		map.put("深圳","755");
		map.put("河源","762");
		map.put("广州","20" );
		map.put("肇庆","758");
		map.put("湛江","759");
		map.put("清远","763");
		map.put("珠海","756");
		map.put("潮州","768");
		map.put("佛山","757");
		map.put("惠州","752");
		map.put("东莞","769");
		map.put("茂名","668");
		map.put("阳江","662");
		map.put("云浮","766");
		map.put("韶关","751");
		map.put("汕头","754");
		map.put("汕尾","660");
		map.put("揭阳","663");
		map.put("梅州","753");
		map.put("江门","750");
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 */
	public static void main(String[] args) throws  Exception {
//		XmlBeanFactory ctx = new XmlBeanFactory(new ClassPathResource("com/hunthawk/reader/test/test.xml"));
//		HibernateGenericController c = (HibernateGenericController)ctx.getBean("hibernateGenericController");
//		
//		MsgBoard board = c.get(MsgBoard.class, 50);
//		System.out.println(board.getName());
//		MsgRecord record = new MsgRecord();
//		record.setBoardId(50);
//		record.setColumnId(300);
//		record.setContent("大记号");
//		record.setCreateTime(new Date());
//		record.setMobile("13110119022");
//		record.setModifyTime(new Date());
//		record.setMsgType(MsgRecord.TYPE_COLUMN);
//		record.setName("ss");
//		record.setStatus(MsgRecord.MSG_STATUS_WAIT_ADUIT);
//		c.save(record);
		
//		String s = "1.zip";
//		System.out.println(s.substring(0, s.length() -4));
//		System.out.println(s.split("\\.").length);
//		
		
//		System.out.println(FileUtils.readFileToString(new File("D:\\华信\\样例包\\上传\\stream1\\streaming\\shiji\\txt\\1.txt")));
		
//		String a = "*Sname:名称";
//		String[] columnFileds = a.split(":");
//		String field = columnFileds[0].substring(2);
//		String desc = columnFileds[1];
//		System.out.println(field+":"+desc);
//		ResourceAll resource = new Ebook();
//		BeanUtils.forceSetProperty(resource, "bLanguage", "asd");
//		System.out.println(resource.getBLanguage());
//		
//		System.out.println(BeanUtils.forceGetProperty(resource,"bLanguage"));
		
//		XmlBeanFactory ctx = new XmlBeanFactory(new ClassPathResource("com/hunthawk/reader/test/resource.xml"));
//		HibernateGenericController c = (HibernateGenericController)ctx.getBean("hibernateGenericController");
	
//		String[] ids = {"1000056",
//				"1000063",
//				"1000061",
//				"1000072",
//				"1000079",
//				"1000085",
//				"1000087",
//				"1000078",
//				"1000082",
//				"1000086",
//				"1000084",
//				"1000053",
//				"1000060",
//				"1000064",
//				"1000073",
//				"1000075",
//				"1000090",
//				"1000089",
//				"1000002",
//				"1000059",
//				"1000066",
//				"1000071",
//				"1000069",
//				"1000070",
//				"1000077",
//				"1000081",
//				"1000083",
//				"1000095",
//				"1000101",
//				"1000102",
//				"1000052",
//				"1000058",
//				"1000065",
//				"1000076",
//				"1000088",
//				"1000096",
//				"1000093",
//				"1000097",
//				"1000001",
//				"1000051",
//				"1000055",
//				"1000057",
//				"1000080",
//				"1000092",
//				"1000094",
//				"1000062",
//				"1000091",
//				"1000099",
//				"1000067",
//				"1000068",
//				"1000074",
//				"1000098"};
//		for(String id : ids){
//			String sql = "insert into reader_resource_all(resource_id,res_name,cp_id,status) values (";
//			sql += "'"+id+"','ceshi','1050',0);";
//			System.out.println(sql);
//			System.out.println(id);
			
//			String sql ="insert into READER_RESOURCE_EBOOK(RESOURCE_ID) values ('"+id+"');";
//			System.out.println(sql);
//		}
		
//		MemCachedClientWrapper memcached = new MemCachedClientWrapper();
//		memcached.setWeightList("1");
//		memcached.setServerList("58.248.254.40:11211");
//		memcached.setName("reader");
//		memcached.setInitConn(1);
//		memcached.init();
//		
		InputStreamReader fr = null;
		BufferedReader br = null;
		try{
			fr = new InputStreamReader(new FileInputStream("D:\\华信\\样例包\\bookUnicom\\bookUnicom\\stream\\copyright.csv"));
			br = new BufferedReader(fr);
			br.readLine();
			int lineNo = 1;
			String rec;
			while ((rec = br.readLine()) != null) {
				String[] argsArr = rec.split(",");
				System.out.println(argsArr.length);
				for(String s : argsArr){
					System.out.println(s);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		List<String> lines = FileUtils.readLines(new File("D:\\resource.csv"));
		StringBuilder str = new StringBuilder();
		int i = 0;
//		int id = 10;
		for(String line : lines){
//			int rm = ((Double)(Math.random()*3000)).intValue()+2000;
//			str+= line+",";
			String[] strs = line.split(",");
//			System.out.println("update reader_resource_all t set t.RES_NAME = '"+strs[1]+"', t.r_keyword='"+strs[2]+"' where t.RESOURCE_ID= '"+strs[0] + "' ;");
//			str.append("update reader_resource_all t set t.RES_NAME='"+strs[1]+"' where t.RESOURCE_ID= '"+strs[0] + "' ;\r\n");
//			if(NumberUtils.isNumber(strs[1])){
//				str.append("update reader_resource_all t set t.DIVISION_N="+strs[1].trim()+" where t.RESOURCE_ID= '"+strs[0] + "' ;\r\n");
//				i++;
//				if(i%100==0){
//					str.append("commit;\r\n");
//				}
//			}
			i++;
			if(i%100==0){
				str.append("commit;\r\n");
			}
			str.append("insert into TB_D_MOBILE_INFORMATION(MOBILE_PREFIX,PROVINCE_ID,PROVINCE_NAME,CITY_NAME,CITY_ID) values ('");
			str.append(strs[1]);
			str.append("','051','广东','");
			str.append(strs[0]);
			str.append("',");
			str.append(map.get(strs[0]));
			str.append(");\r\n");
			
//			str.append("insert into reader_area_user(mobile,area_id,city_id,brand) values ('");
//			str.append(strs[1]);
//			str.append("','051','");
//			str.append(strs[0]);
//			str.append("','");
//			str.append(strs[3].trim());
//			str.append("');\r\n");
			//			System.out.println(line);
//			str.append("update reader_bussiness_column t set t.list_order = "+(id++)+" where t.list_id="+line+";\r\n"); 
		}
		FileUtils.writeStringToFile(new File("D:\\resource.sql"), str.toString());
//		System.out.println(str);
//		char c = 'a';
//		while(c<='z'){
//			String in = String.valueOf(c);
//			System.out.println("update  reader_resource_all t set t.INITIAL_LETTER='"+in.toUpperCase()+"' where t.INITIAL_LETTER = '"+c+"';");
//			c++;
//		}
		
//		List<String> lines = FileUtils.readLines(new File("D:\\author.txt"));
//		String sql = "";
//		for(String line : lines){
//			Integer id = Integer.parseInt(line.trim());
//			String filepath = "D:\\author\\"+id/1000+"\\"+id+".jpg";
//			FileUtils.copyFile(new File("D:\\author.jpg"),new File(filepath));
//			sql += " update reader_resource_author t set t.AUTHOR_PIC = 'author/"+id/1000+"/"+id+".jpg' where t.author_id="+id+";\r\n";
//			System.out.println(filepath);
//		}
//		FileUtils.writeStringToFile(new File("D:\\author.sql"), sql);
		
//		is_out
//		Calendar today = Calendar.getInstance();
//		today.set(Calendar.DAY_OF_MONTH, 23);
//		System.out.println(today);
//		Date endDate = DateUtils.truncate(DateUtils.addDays(today.getTime(), (-1) * (today.get(Calendar.DAY_OF_WEEK) - 2)), Calendar.DATE);
//		Date startDate = DateUtils.addDays(endDate, -7);
//		
//		System.out.println( startDate);
//		System.out.println( endDate);
		
//		String str = "你好";
//		String str2 = "大家庭院";
//		String str3 = "大家的话";
//		change(str);
//		change(str2);
//		change(str3);
		
	}
	
	static void change(String value)throws Exception{
		System.out.println("VALUE="+value);
		String iso =  new String(value.getBytes("UTF-16"), "iso-8859-1");
		System.out.println("ISO="+iso);
		String gbk =  new String(value.getBytes("UTF-16"), "GBK");
		System.out.println("GBK="+gbk);
		String gbk2 =  new String(value.getBytes("UTF-16"), "GB2312");
		System.out.println("GBK2="+gbk2);
		
		String gbk3 =  new String(value.getBytes("UTF-16"), "UTF-8");
		System.out.println("GBK2="+gbk3);
		
		String newiso = new String(value.getBytes(), "iso-8859-1");
		System.out.println("NEWISO="+newiso);
		String newgbk =  new String(value.getBytes(), "GBK");
		System.out.println("newgbk="+newgbk);
	}

}
