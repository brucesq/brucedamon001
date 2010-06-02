package com.hunthawk.reader.domain.resource;

import com.hunthawk.framework.domain.PersistentObject;

public class EbookKeyWord implements java.io.Serializable{
	 private int HASHCODE= Integer.MIN_VALUE;    

	public EbookKeyWord(){}
	private String id;
	/**
	 * 资源名称
	 */
	private String name;
	/**
	 * CP表中的ID
	 */
	private Integer cpId;
	
	/**
	* 卷名称
	*/
	private String tomeName;
	/**
	* 章节名称
	*/
	private String chapterName;
	/**
	 * 章节ID
	 */
	private String chapterId;
	public String getChapterId() {
		return chapterId;
	}
	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}
	/**
	 * 敏感字内容
	 */
	private String keyWordConent;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}
	public String getTomeName() {
		return tomeName;
	}
	public void setTomeName(String tomeName) {
		this.tomeName = tomeName;
	}
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	public String getKeyWordConent() {
		return keyWordConent;
	}
	public void setKeyWordConent(String keyWordConent) {
		this.keyWordConent = keyWordConent;
	}
	 public int hashCode() {     
	        if (HASHCODE == Integer.MIN_VALUE) {     
	           // 重新生成本类的hashCode     
	          //HASHCODE = name.hashCode() + keyWordConent.hashCode();   
	        	HASHCODE =id.hashCode()+ chapterId.hashCode()+name.hashCode()+ keyWordConent.hashCode();  
	        }     
	        return HASHCODE;     
	      }     
	        
	    //判断值是否相等    
	    public boolean equals(Object obj) {    
	        if(obj == null || !(obj  instanceof  EbookKeyWord)){    
	            return false;    
	        }    
	        EbookKeyWord ekw = (EbookKeyWord)obj;    
	        return this.name == ekw.name && this.keyWordConent ==  ekw.keyWordConent;    
	    }    

}
