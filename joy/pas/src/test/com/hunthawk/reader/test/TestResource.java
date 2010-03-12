/**
 * 
 */
package com.hunthawk.reader.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author BruceSun
 *
 */
public class TestResource {

	public static void main(String[] args){
//		XmlBeanFactory ctx = new XmlBeanFactory(new ClassPathResource("com/hunthawk/reader/test/resource.xml"));
//		MaterialService c = (MaterialService)ctx.getBean("materialService");
//		ResourcePack pack = new ResourcePack();
//		pack.setCode("2");
//		pack.setCreateTime(new Date());
//		pack.setCreator(1);
//		pack.setFeeId("211001000002");
//		pack.setName("VIP");
//		pack.setSpid("11001000");
//		pack.setType(Constants.FEE_TYPE_VIP);
//		c.save(pack);
//		
//		ResourcePack pack1 = new ResourcePack();
//		pack1.setCode("2");
//		pack1.setCreateTime(new Date());
//		pack1.setCreator(1);
//		pack1.setFeeId("211001000001");
//		pack1.setName("N选X");
//		pack1.setSpid("11001000");
//		pack1.setType(Constants.FEE_TYPE_CHOICE);
//		c.save(pack1);
		
		
//		ResourcePack pack2 = new ResourcePack();
//		pack2.setCode("5");
//		pack2.setCreateTime(new Date());
//		pack2.setCreator(1);
//		pack2.setFeeId("221002099001");
//		pack2.setName("普通包月");
//		pack2.setSpid("21002099");
//		pack2.setType(Constants.FEE_TYPE_NORMAL);
//		c.save(pack2);
		
//		System.out.println(c.getMaterialCatalog(2).getName());
		
		InputStreamReader fr = null;
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try{
			fr = new InputStreamReader(new FileInputStream("D:\\mobile1.txt"));
			br = new BufferedReader(fr);
			
			int lineNo = 1;
			String rec;
			while ((rec = br.readLine()) != null) {
				String[] argsArr = rec.split("\\?");
				if(!list.contains(argsArr[0]) && argsArr[0].length()>10){
					list.add(argsArr[0]);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(list.size());
		StringBuilder builder = new StringBuilder();
		for(String s : list){
			builder.append(s);
			builder.append("\r\n");
		}
		try{
			FileUtils.writeStringToFile(new File("D:\\mobile1217.txt"), builder.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
