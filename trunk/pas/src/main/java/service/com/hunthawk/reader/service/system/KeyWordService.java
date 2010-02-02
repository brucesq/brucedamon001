package com.hunthawk.reader.service.system;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.resource.ResourcePack;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.enhance.annotation.Memcached;

public interface KeyWordService {
	
	/**
	 * 单个增加关键字
	 */
	@Logable(name = "KeyWord", action = "add", property = { "id=ID,keyWord=敏感词,type.type=类型,creator=创建人,createTime=创建时间" })
	public String addKeyWord(KeyWord keyWord)throws Exception;
	
	/**
	 * 删除关键字
	 */
	@Logable(name = "KeyWord", action = "delete", property = { "id=ID,keyWord=敏感词,type.type=类型,creator=创建人,createTime=创建时间" })
	public void delKeyWord(KeyWord keyWord)throws Exception;
	
	/**
	 * 更新关键字
	 */
	@Logable(name = "KeyWord", action = "update", property = { "id=ID,keyWord=敏感词,type.type=类型,creator=创建人,createTime=创建时间" })
	public String updateKeyWord(KeyWord keyWord)throws Exception;
	
	/**
	 * 关键字列表
	 */
	@SuppressWarnings("unchecked")
	public List findKeyWordBy( int pageNo, int pageSize, String orderBy, boolean isAsc,Collection<HibernateExpression> expressions);
	/**
	 * 关键字个数
	 */
	@SuppressWarnings("unchecked")
	public Long getKeyWordResultCount( Collection<HibernateExpression> expressions );
	
	/**
	 * 查询单个关键字
	 */
	
	public Object getKeyWord(Integer id);
	
	/**
	 * 关键字类型管理
	 */
	
	@Logable(name = "KeyWordType", action = "add", property = { "id=ID,type=敏感词类型名称,creator=创建人,createTime=创建时间" })
	public void addKeyWordType(KeyWordType type);
	
	@Logable(name = "KeyWordType", action = "update", property = { "id=ID,type=敏感词类型名称,creator=创建人,createTime=创建时间" })
	public void updateKeyWordType(KeyWordType type);
	
	@Logable(name = "KeyWordType", action = "delete", property = { "id=ID,type=敏感词类型名称,creator=创建人,createTime=创建时间" })
	public void delKeyWordType(KeyWordType type);
	
	public List<KeyWordType> getKeyWordTypeList(int pageNo, int pageSize, String orderBy, boolean isAsc,Collection<HibernateExpression> expressions);
	
	public KeyWordType getKeyWordType(Integer id);
	
	public Long getKeyWordTypeCount(Collection<HibernateExpression> expressions );
}
