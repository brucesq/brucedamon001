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
	@Logable(name = "Fee", action = "add", property = { "id=ID,name=����,serviceId=serviceId,productId=productId,code=�۸�,comment=����,type=�Ʒ�����,status=״̬,isout=�Ƿ񵯳��ʷ�ҳ,templateId=�ʷ�ҳģ��,portalUrl=��ڵ�ַ,actionUrl=ȷ�ϵ�ַ,url=�Ʒѵ�ַ,flag=ɾ����ʶ,discount=�Ƿ��ۿ�,provider.id=�ṩ��ID,createTime=����ʱ��,createorId=������,modifyTime=�޸�ʱ��,motifierId=�޸���" })
	public void addFee(Fee fee)throws Exception;
	
	@Memcached(targetClass=Fee.class,properties={"id","url"})
	@Logable(name = "Fee", action = "update", property = { "id=ID,name=����,serviceId=serviceId,productId=productId,code=�۸�,comment=����,type=�Ʒ�����,status=״̬,isout=�Ƿ񵯳��ʷ�ҳ,templateId=�ʷ�ҳģ��,portalUrl=��ڵ�ַ,actionUrl=ȷ�ϵ�ַ,url=�Ʒѵ�ַ,flag=ɾ����ʶ,discount=�Ƿ��ۿ�,provider.id=�ṩ��ID,createTime=����ʱ��,createorId=������,modifyTime=�޸�ʱ��,motifierId=�޸���" })
	public void updateFee(Fee fee)throws Exception;
	
	@Memcached(targetClass=Fee.class,properties={"id","url"})
	@Logable(name = "Fee", action = "delete", property = { "id=ID,name=����,serviceId=serviceId,productId=productId,code=�۸�,comment=����,type=�Ʒ�����,status=״̬,isout=�Ƿ񵯳��ʷ�ҳ,templateId=�ʷ�ҳģ��,portalUrl=��ڵ�ַ,actionUrl=ȷ�ϵ�ַ,url=�Ʒѵ�ַ,flag=ɾ����ʶ,discount=�Ƿ��ۿ�,provider.id=�ṩ��ID,createTime=����ʱ��,createorId=������,modifyTime=�޸�ʱ��,motifierId=�޸���" })
	@Restrict( roles = { "feeaudit" }, mode = Restrict.Mode.ROLE)
	public void deleteFee(Fee fee)throws Exception;
	
	public List<Fee> findFee(int pageNo,int pageSize,String orderBy,
			boolean isAsc,Collection<HibernateExpression> expressions);
	
	public long findFeeResultCount(Collection<HibernateExpression> expressions);
	
	public Fee getFee(String id);
	
	@Memcached(targetClass=Fee.class,properties={"id","url"})
	@Logable(name = "Fee", action = "audit", property = { "id=ID,name=����,serviceId=serviceId,productId=productId,code=�۸�,comment=����,type=�Ʒ�����,status=״̬,isout=�Ƿ񵯳��ʷ�ҳ,templateId=�ʷ�ҳģ��,portalUrl=��ڵ�ַ,actionUrl=ȷ�ϵ�ַ,url=�Ʒѵ�ַ,flag=ɾ����ʶ,discount=�Ƿ��ۿ�,provider.id=�ṩ��ID,createTime=����ʱ��,createorId=������,modifyTime=�޸�ʱ��,motifierId=�޸���" })
	@Restrict( roles = { "feeaudit" }, mode = Restrict.Mode.ROLE)
	public void auditFee(Fee fee,int status)throws Exception;
}
