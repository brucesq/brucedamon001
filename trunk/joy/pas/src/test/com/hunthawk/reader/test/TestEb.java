/**
 * 
 */
package com.hunthawk.reader.test;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.reader.domain.bussiness.TagTemplate;

/**
 * @author BruceSun
 * 
 */
public class TestEb {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws Exception {
		// Long l = 12345098765L;
		// FileOutputStream fout = new
		// FileOutputStream("d:\\readermmcache.log");
		// DataOutputStream out = new DataOutputStream(fout);
		// out.writeLong(l);
		// out.flush();
		// out.close();
		// fout.close();
		//
		// FileInputStream fin = new FileInputStream("d:\\readermmcache.log");
		// DataInputStream in = new DataInputStream(fin);
		// System.out.println(in.readLong());

//		String str = "material/0/1";

		// System.out.println(str.split("\\|").length);
		// System.out.println(str.startsWith("|"));
		// System.out.println(str.replaceAll("\\/", "\\\\"));

//		FileOutputStream fout = new FileOutputStream("d:\\1.ueb");
//		DataOutputStream out = new DataOutputStream(fout);
//		out.writeBytes("aplication/x-ueb");
//		out.writeInt(1);
//		out.writeLong(Long.parseLong("10000001"));
//		out.writeInt(1);
//		out.writeInt(1);
//		out.writeLong(1L);
//
//		out.flush();
//		out.close();
//		fout.close();

//		File file = new File("d:\\1.ueb");
//		RandomAccessFile raf = new RandomAccessFile(file, "rw");
//		byte[] b = new byte[880];
//		raf.seek(55068);
//		raf.read(b);
//		byte[] b1 = GZIPUtil.ungzip(b);
//		FileUtils.writeByteArrayToFile(new File("d:\\1.jpg"), b1);
//		System.out.println(new String(b1,"utf-8"));
		
//		raf.seek(76);
//		System.out.println(raf.readLong());
		
		
//		XmlBeanFactory ctx = new XmlBeanFactory(new ClassPathResource("com/hunthawk/reader/test/test.xml"));
//		HibernateGenericController c = (HibernateGenericController)ctx.getBean("hibernateGenericController");
//		TagTemplate tagTemplate = new TagTemplate();
//		tagTemplate.setHtmlContent(FileUtils.readFileToString(new File("D:\\iresourcelist.txt")));
//		tagTemplate.setWmlContent(FileUtils.readFileToString(new File("D:\\iresourcelist.txt")));
//		tagTemplate.setName("IPHONE资源列表");
//		tagTemplate.setTagName("iresource_list");
//		c.save(tagTemplate);
//		ResourcePackService resourcePackService = (ResourcePackService)ctx.getBean("resourcePackService");
//		ResourcePack pack = new ResourcePack();
//		pack.setId(5751);
//		System.out.println(resourcePackService.getResourcePackReleationResultCountByHQL(null, pack, null, null,0));
//		PartnerService partnerService = (PartnerService)ctx.getBean("partnerService");
//		Provider provider = partnerService.getProvider(5005);
//		Channel channel = new Channel();
//		channel.setId(String.valueOf(provider.getId()));
//		channel.setAddress(provider.getAddress());
//		channel.setBankAccount(provider.getBankAccount());
//		channel.setBankName(provider.getBankName());
//		channel.setBankAccountName(provider.getBankAccountName());
//		channel.setChannelTypeId(6);
//		channel.setChName(provider.getChName());
//		channel.setCity(provider.getCity());
//		channel.setContactEmail(provider.getContactEmail());
//		channel.setContactJob(provider.getContactJob());
//		channel.setContactMobile(provider.getContactMobile());
//		channel.setContactName(provider.getContactName());
//		channel.setContactPhone(provider.getContactPhone());
//		channel.setCorporate(provider.getCorporate());
//		channel.setCredit(provider.getCredit());
//		channel.setCreateorId(provider.getCreateorId());
//		channel.setCreateTime(provider.getCreateTime());
//		channel.setFax(provider.getFax());
//		channel.setIntro(provider.getIntro());
//		channel.setModifyTime(provider.getModifyTime());
//		channel.setMotifierId(provider.getMotifierId());
//		channel.setPhone(provider.getPhone());
//		channel.setPostcode(provider.getPostcode());
//		channel.setProportion(provider.getProportion());
//		channel.setProportionTime(provider.getProportionTime());
//		channel.setRegisteredCapita(provider.getRegisteredCapita());
//		channel.setUrl(provider.getUrl());
//		partnerService.addChannel(channel);
//		UebService ueb = (UebService)ctx.getBean("uebService");
//		ResourcePackReleation rel = new ResourcePackReleation();
//		ResourcePack pack = new ResourcePack();
//		pack.setId(50);
//		rel.setCpid("21002099");
//		rel.setId(53);
//		rel.setPack(pack);
//		rel.setResourceId("20000001");
//		rel.setFeeId("321002099001");
//		ueb.createUeb(rel);
//		
//		MonthChoiceClearJob job = (MonthChoiceClearJob)ctx.getBean("monthChoiceClearJob");
//		job.doJob();
//		HibernateGenericController c = (HibernateGenericController)ctx.getBean("hibernateGenericController");
//		String hql = "select distinct  buy.mobile from UserBuy buy order by buy.mobile";
//		List<String> mobiles = c.findBy(hql, 1, 100);
//		for(String mobile : mobiles){
//			System.out.println(mobile);
//		}
//		hql = "select count(distinct  buy.mobile) from UserBuy buy order by buy.mobile";
//		List<Integer> result = c.findBy(hql);
//		System.out.println(result.get(0));
//		
//		Session session = c.getHibernateTemplate().getSessionFactory().openSession();
//		Transaction tran = session.beginTransaction();
//        session.setCacheMode(CacheMode.IGNORE); 
//        PreparedStatement stmt;
//        try {
//            stmt = session.connection().prepareStatement("INSERT INTO READER_INTER_REMIND_MOBILE(EVENT_DATE, title) VALUES(?,?)");
//            for (String mobile : mobiles) {
               
//                stmt.setString(2, "Title["+i+"]");
//                stmt.addBatch();
//            }
//            stmt.executeBatch();
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            tran.rollback();
//        }
//        tran.commit();
//        SessionFactoryUtils.closeSession(session);
		String f = "/asd/asd/ssa.jpg";
		System.out.println(f.replaceAll("\\.", "240."));
       
	}

}
