/**
 * 
 */
package com.hunthawk.reader.page.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IDirect;
import org.apache.tapestry.IJSONRender;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.IScript;
import org.apache.tapestry.PageRenderSupport;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.dojo.form.AbstractFormWidget;
import org.apache.tapestry.dojo.form.IAutocompleteModel;
import org.apache.tapestry.engine.DirectServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.form.ValidatableField;
import org.apache.tapestry.form.ValidatableFieldSupport;
import org.apache.tapestry.json.IJSONWriter;
import org.apache.tapestry.json.JSONArray;
import org.apache.tapestry.json.JSONLiteral;
import org.apache.tapestry.json.JSONObject;
import org.apache.tapestry.services.DataSqueezer;
import org.apache.tapestry.valid.ValidatorException;

/**
 * @author BruceSun
 *
 */
public abstract class AutoSuggest extends AbstractFormWidget implements ValidatableField, IJSONRender, IDirect
{
    // mode, can be remote or local (local being from html rendered option elements)
    //private static final String MODE_REMOTE = "remote";
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void renderFormWidget(IMarkupWriter writer, IRequestCycle cycle)
    {
        renderDelegatePrefix(writer, cycle);
        
        writer.begin("input");
        writer.attribute("name", getName());
        writer.attribute("type", "text"); 
        
        if (isDisabled())
            writer.attribute("disabled", "disabled");
        
        renderIdAttribute(writer, cycle);
        
        renderDelegateAttributes(writer, cycle);
        
        getValidatableFieldSupport().renderContributions(this, writer, cycle);
        
        // Apply informal attributes.
        renderInformalParameters(writer, cycle);
        
        writer.end();
        
        writer.begin("input");
        writer.attribute("name", getName()+"_value");
        writer.attribute("type", "hidden"); 
        writer.attribute("id", getName()+"_value");
        writer.end();
        
        
        renderDelegateSuffix(writer, cycle);
        
        ILink link = getDirectService().getLink(true, new DirectServiceParameter(this));
        
        Map parms = new HashMap();
        parms.put("id", getClientId());
        parms.put("name", getName());
        JSONObject json = new JSONObject();
        json.put("script", link.getURL()+"&rmt="+System.currentTimeMillis()+"&");
        json.put("varname", "filter");
        json.put("json", "true");
        json.put("delay", getSearchDelay());
        json.put("timeout", getFadeTime());
        json.put("maxresults", getMaxListLength());
        json.put("callback", new JSONLiteral(" function (obj) { document.getElementById('"+getName()+"_value"+"').value = obj.id; }"));
        
        IAutocompleteModel model = getModel();
        if (model == null)
            throw Tapestry.createRequiredParameterException(this, "model");
        
        Object value = getValue();
        Object key = value != null ? model.getPrimaryKey(value) : null;
        
        if (value != null && key != null) {
            
            json.put("value", getDataSqueezer().squeeze(key));
            json.put("label", model.getLabelFor(value));
        }
        
        parms.put("props", json.toString());
        parms.put("form", getForm().getName());
        parms.put("widget", this);
        
        PageRenderSupport prs = TapestryUtils.getPageRenderSupport(cycle, this);
        getScript().execute(this, cycle, prs, parms);
    }
    
    /**
     * {@inheritDoc}
     */
    public void renderComponent(IJSONWriter writer, IRequestCycle cycle)
    {
        IAutocompleteModel model = getModel();
        if (model == null)
            throw Tapestry.createRequiredParameterException(this, "model");
        
        List filteredValues = model.getValues(getFilter());
        
        if (filteredValues == null)
            return;
        
        Object key = null;
        String label = null;
       
        
        JSONObject json = writer.object();
        JSONArray array = new JSONArray();      
        for (int i=0; i < filteredValues.size(); i++) {
        	
            Object value = filteredValues.get(i);
            key = model.getPrimaryKey(value);
            label = model.getLabelFor(value);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id",getDataSqueezer().squeeze(key));
            jsonObj.put("value", label); 
            jsonObj.put("info", "");
            array.put(jsonObj);
        }      
        json.put("results", array);
    }
    
    /**
     * @see org.apache.tapestry.form.AbstractFormComponent#rewindFormComponent(org.apache.tapestry.IMarkupWriter, org.apache.tapestry.IRequestCycle)
     */
    protected void rewindFormWidget(IMarkupWriter writer, IRequestCycle cycle)
    {
        String value = cycle.getParameter(getName()+"_value");
        String label = cycle.getParameter(getName());
        Object object = null;
        
        try
        {
            if (value != null && value.length() > 0)
                object = getModel().getValue(getDataSqueezer().unsqueeze(value));
            
            getValidatableFieldSupport().validate(this, writer, cycle, object);
            if(object != null)
            {
            	if(!getModel().getLabelFor(object).equals(label))
            	   object = null;
            }
            setValue(object);
        }
        catch (ValidatorException e)
        {
            getForm().getDelegate().record(e);
        }
    }
    
    /** 
     * {@inheritDoc}
     */
    public boolean isStateful()
    {
        return true;
    }
    
    /**
     * Triggerd by using filterOnChange logic.
     * 
     * {@inheritDoc}
     */
    public void trigger(IRequestCycle cycle)
    {
        setFilter(cycle.getParameter("filter"));
    }
    
    public abstract IAutocompleteModel getModel();
    
    /** How long to wait(in ms) before searching after input is received. */
    public abstract int getSearchDelay();
    
    /** The duration(in ms) of the fade effect of list going away. */
    public abstract int getFadeTime();
    
    /** The maximum number of items displayed in select list before the scrollbar is activated. */
    public abstract int getMaxListLength();
    
    /** Forces select to only allow valid option strings. */
    public abstract boolean isForceValidOption();
    
    /** @since 2.2 * */
    public abstract Object getValue();

    /** @since 2.2 * */
    public abstract void setValue(Object value);
    
    /** @since 4.1 */
    public abstract void setFilter(String value);
    
    /** @since 4.1 */
    public abstract String getFilter();
    
    /** Injected. */
    public abstract DataSqueezer getDataSqueezer();
    
    /**
     * Injected.
     */
    public abstract ValidatableFieldSupport getValidatableFieldSupport();

    /**
     * Injected.
     */
    public abstract IEngineService getDirectService();
    
    /**
     * Injected.
     */
    public abstract IScript getScript();
    
    /**
     * @see org.apache.tapestry.form.AbstractFormComponent#isRequired()
     */
    public boolean isRequired()
    {
        return getValidatableFieldSupport().isRequired(this);
    }

    /** 
     * {@inheritDoc}
     */
    public List getUpdateComponents()
    {
        List comps = new ArrayList();
        comps.add(getId());
        
        return comps;
    }
    
    /** 
     * {@inheritDoc}
     */
    public boolean isAsync()
    {
        return true;
    }
    
    /** 
     * {@inheritDoc}
     */
    public boolean isJson()
    {
        return true;
    }
}