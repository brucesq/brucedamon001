/**
 * 
 */
package com.hunthawk.reader.domain.resource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hunthawk.framework.domain.PersistentObject;
import com.hunthawk.reader.enhance.util.ToolDateUtil;

/**
 * ËØ²ÄÄ¿Â¼
 * @author BruceSun
 *
 */
@Entity
@Table(name = "reader_resource_material_cata")
public class MaterialCatalog  extends PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3492307246903592659L;
	
	public static final Integer TYPE_IMAGE = 1;
	
	public static final Integer TYPE_MUSIC = 2;
	
	private static Map<String,Integer> TYPE = new HashMap<String,Integer>();
	static{
		TYPE.put("Í¼Æ¬", 1);
		TYPE.put("ÒôÀÖ", 2);
	}
	
	public static Map<String,Integer> getMaterialType(){
		return TYPE;
	}

	private Integer id;
	
	private String name;
	//ËØ²ÄÀà±ð1Í¼Æ¬¡¢2ÒôÀÖ
	private Integer type;
	
	private Integer creator;
    
    private Date createTime;
    
    private Integer modifier;
    
   
    
    private Date modifyTime;

    @Id
	@GeneratedValue(generator = "system-auto")
	@GenericGenerator(name = "system-auto", strategy = "increment")
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "creator_id")
	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "modifier_id")
	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}
	@Column(name = "modify_time")
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Column(name = "cata_type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	@Transient
    public String getTypeName(){
		for(Map.Entry<String, Integer> entry:TYPE.entrySet()){
			if(entry.getValue().equals(type)){
				return entry.getKey();
			}
		}
		return "";
    }
	@Transient
	public String getShowModifyTimeName() {
		if(modifyTime != null){
			return ToolDateUtil.dateToString(modifyTime);
		}else{
			return "";
		}
		
	}
}
