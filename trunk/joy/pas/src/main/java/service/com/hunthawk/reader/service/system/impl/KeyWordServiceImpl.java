package com.hunthawk.reader.service.system.impl;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.HibernateGenericController;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.service.system.KeyWordService;

public class KeyWordServiceImpl implements KeyWordService {
	private HibernateGenericController controller ;
	
	public void setHibernateGenericController(HibernateGenericController controller){
		this.controller = controller;
	}
	public String addKeyWord(KeyWord keyWord) throws Exception{
		String message="";
		if(controller.isUnique(KeyWord.class, keyWord, "keyWord,type"))//敏感词和类型的唯一
			controller.save(keyWord);	
		else
			message += keyWord.getKeyWord()+",";
		return message;
	}

	public void delKeyWord(KeyWord keyWord) {
		
		controller.delete(keyWord);
	}

	public List findKeyWordBy(int pageNo, int pageSize, String orderBy,
			boolean isAsc, Collection<HibernateExpression> expressions) {
		return controller.findBy(KeyWord.class, pageNo, pageSize,orderBy,isAsc,expressions);
	}

	public Object getKeyWord(Integer id) {
		return controller.get(KeyWord.class, id);
	}

	public Long getKeyWordResultCount(
			Collection<HibernateExpression> expressions) {
		return controller.getResultCount(KeyWord.class, expressions);
	}

	public String updateKeyWord(KeyWord keyWord) throws Exception{
		String message="";
		if(controller.isUnique(KeyWord.class, keyWord, "keyWord,type"))
			controller.update(keyWord);	
		else
			message += keyWord.getKeyWord()+",";
		return message;
	}
	public void addKeyWordType(KeyWordType type) {
		controller.save(type);
	}
	public void delKeyWordType(KeyWordType type) {
		controller.delete(type);
	}
	public KeyWordType getKeyWordType(Integer id) {
		return controller.get(KeyWordType.class, id);
	}
	public List<KeyWordType> getKeyWordTypeList(int pageNo, int pageSize,
			String orderBy, boolean isAsc,
			Collection<HibernateExpression> expressions) {
		return controller.findBy(KeyWordType.class, pageNo, pageSize,orderBy,isAsc,expressions);
	}
	public void updateKeyWordType(KeyWordType type) {
		controller.update(type);
	}
	public Long getKeyWordTypeCount(Collection<HibernateExpression> expressions) {
		return controller.getResultCount(KeyWordType.class,expressions);
	}

}
