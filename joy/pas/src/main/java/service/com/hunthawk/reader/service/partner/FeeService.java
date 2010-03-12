/**
 * 
 */
package com.hunthawk.reader.service.partner;

import java.util.Collection;
import java.util.List;

import com.hunthawk.framework.annotation.Logable;
import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.reader.domain.partner.Fee;
import com.hunthawk.reader.enhance.annotation.Memcached;

/**
 * @author BruceSun
 *
 */
public interface FeeService {

	@Memcached(targetClass=Fee.class,properties={"id","url"})
	@Logable(name = "Fee", action = "add", property = { "id=ID,name=名称,serviceId=serviceId,productId=productId,code=价格,comment=描述,type=计费类型,status=状态,isout=是否弹出资费页,templateId=资费页模板,portalUrl=入口地址,actionUrl=确认地址,url=计费地址,flag=删除标识,discount=是否折扣,provider.id=提供商ID,createTime=创建时间,createorId=创建人,modifyTime=修改时间,motifierId=修改人" })
	public void addFee(Fee fee)throws Exception;
	
	@Memcached(targetClass=Fee.class,properties={"id","url"})
	@Logable(name = "Fee", action = "update", property = { "id=ID,name=名称,serviceId=serviceId,productId=productId,code=价格,comment=描述,type=计费类型,status=状态,isout=是否弹出资费页,templateId=资费页模板,portalUrl=入口地址,actionUrl=确认地址,url=计费地址,flag=删除标识,discount=是否折扣,provider.id=提供商ID,createTime=创建时间,createorId=创建人,modifyTime=修改时间,motifierId=修改人" })
	public void updateFee(Fee fee)throws Exception;
	
	@Memcached(targetClass=Fee.class,properties={"id","url"})
	@Logable(name = "Fee", action = "delete", property = { "id=ID,name=名称,serviceId=serviceId,productId=productId,code=价格,comment=描述,type=计费类型,status=状态,isout=是否弹出资费页,templateId=资费页模板,portalUrl=入口地址,actionUrl=确认地址,url=计费地址,flag=删除标识,discount=是否折扣,provider.id=提供商ID,createTime=创建时间,createorId=创建人,modifyTime=修改时间,motifierId=修改人" })
	@Restrict( roles = { "feeaudit" }, mode = Restrict.Mode.ROLE)
	public void deleteFee(Fee fee)throws Exception;
	
	public List<Fee> findFee(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findFeeResultCount(Collection<HibernateExpression> expressions);
	
	public Fee getFee(String id);
	
	@Memcached(targetClass=Fee.class,properties={"id","url"})
	@Logable(name = "Fee", action = "audit", property = { "id=ID,name=名称,serviceId=serviceId,productId=productId,code=价格,comment=描述,type=计费类型,status=状态,isout=是否弹出资费页,templateId=资费页模板,portalUrl=入口地址,actionUrl=确认地址,url=计费地址,flag=删除标识,discount=是否折扣,provider.id=提供商ID,createTime=创建时间,createorId=创建人,modifyTime=修改时间,motifierId=修改人" })
	@Restrict( roles = { "feeaudit" }, mode = Restrict.Mode.ROLE)
	public void auditFee(Fee fee,int status)throws Exception;
}
