/**
 * 
 */
package com.hunthawk.reader.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;

import com.hunthawk.framework.tapestry.SecurityPage;
import com.hunthawk.framework.util.BeanUtils;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.page.util.DetailPageField;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
public abstract class DetailPage extends SecurityPage {

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();
	
	@Asset("img/Toolbar_bg.png")
	public abstract IAsset getBackGroundIcon();
	
	

	public abstract void setCurrentObject(Object obj);

	public abstract  Object getCurrentObject();

	public List<DetailPageField> getDetailList(){
		
		return getObjectFields(getCurrentObject());
	}

	

	public abstract DetailPageField getCurrentDetailField();

	

	private List<DetailPageField> getObjectFields(Object obj) {

		Variables variables = getSystemService().getVariables(
				"detail:" + getCurrentObject().getClass().getName());
		List<DetailPageField> details = new ArrayList<DetailPageField>();
		String desc = variables.getValue();
		String[] props = desc.split(",");
		for (String prop : props) {
			DetailPageField field = new DetailPageField();
			String[] kv = prop.split("=");
			if(kv.length < 2){
				continue;
			}
			field.setTitle(kv[1]);
			field.setValue(getObjectField(obj, kv[0]));
			details.add(field);
		}
		return details;
	}

	private String getObjectField(Object obj, String prop) {
		if (prop.startsWith("@")) {
			Object target = getProperty(obj,prop.substring(1));
			List<DetailPageField> fields = getObjectFields(target);
			StringBuilder builder = new StringBuilder();
			for(DetailPageField field : fields){
				builder.append(field.getTitle());
				builder.append(":");
				builder.append(field.getValue());
				builder.append("\r\n");
			}
			return builder.toString();
		}else{
			return getProperty(obj,prop).toString();
		}
		
	}
	private Object getProperty(Object obj,String prop){
		String[] propertys = prop.split("\\.");
		try{
			Object value = obj;
			for(String str : propertys){
				value = BeanUtils.forceGetProperty(value, str);
			}
			return value.toString();
		}catch(Exception e){
			logger.error("Get prop error class["+obj.getClass().getName()+"],prop["+prop+"]");
			return "";
		}
	}
}
