/**
 * 
 */
package com.hunthawk.framework.security;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import com.hunthawk.reader.security.PowerUtil;

/**
 * @author sunquanzhi
 *
 */
public class RulebasedIdentity implements Identity , BeanNameAware, ResourceLoaderAware {

	private static final Logger log = Logger.getLogger(RulebasedIdentity.class);

	private String beanName;

	private WorkingMemory securityContext;

	private Resource dslFile;

	private ResourceLoader resourceLoader;

	private RuleBase ruleBase;

	private String[] ruleFiles;
	
	public boolean hasPermission(PermissionCheck permissionCheck,
			Object... args) {
		
		if(SecurityContextHolder.getContext().getUser() == null){
			return true;
		}
		
		if(SecurityContextHolder.getContext().getUser().isAdmin())
		{
			return true;
		}
		if(args == null){
			return true;
		}
		
		 synchronized( securityContext )
		 {
			 List<FactHandle> handles = new ArrayList<FactHandle>();
			 handles.add(securityContext.assertObject(permissionCheck));
			 for(Object arg : args)
			 {
				// log.info("ARG:"+arg.toString());
				 handles.add(securityContext.assertObject(arg));
			 }

			 try{
				User user = SecurityContextHolder.getContext().getUser();
				if(user != null)
				{
					 handles.add(securityContext.assertObject(user));
				}
			 }catch(Exception e){}
			 
			 
			 securityContext.fireAllRules();
			 for (FactHandle handle : handles)
		            securityContext.retractObject(handle);
		 }
		 
		 return permissionCheck.isGranted(); 
	}

	
	public boolean hasRole(String role) {
		try{
			User user = SecurityContextHolder.getContext().getUser();
			return user.isAdmin() || user.hasRole(role);
		}catch(Exception e){
			log.error("Has role exception:", e);
			return false;
		}
		
	}

	public void setBeanName(String name) {
		beanName = name;
	}

	public void setDslFile(Resource dslFile) {
		this.dslFile = dslFile;
	}

	
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void init()
	{
		securityContext = getRuleBase().newWorkingMemory();
		
		securityContext.setGlobal("power", new PowerUtil());
	}
	public void setRuleFiles(String[] ruleFiles) {
		this.ruleFiles = ruleFiles;
	}
	private RuleBase getRuleBase() {

		if (ruleBase == null)
			try {
				compileRuleBase();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		return ruleBase;
	}
	/**
	 * <p>±‡“ÎRuleBase</p>
	 * @throws Exception
	 */
	private void compileRuleBase() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		ruleBase = RuleBaseFactory.newRuleBase();
		if (ruleFiles != null) {			
			Reader dslReader = null;
			if (dslFile != null)
				dslReader = new InputStreamReader(dslFile.getInputStream(), "UTF-8");

		
			ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
			for (String rulePattern : ruleFiles) {
				Resource[] rules = resolver.getResources(rulePattern);

				for (Resource ruleFile : rules) {
					PackageDescr packageDescr;
					Reader drlReader = new InputStreamReader(ruleFile.getInputStream(), "UTF-8");
					
					if (dslFile != null)
						packageDescr = new DrlParser().parse(drlReader, dslReader);
					else
						packageDescr = new DrlParser().parse(drlReader);					
					builder.addPackage(packageDescr);
				}
			}
			ruleBase.addPackage(builder.getPackage());
		} else
			log.warn("DroolsTemplate" + beanName + "didn't set the rule files");
	}

}
