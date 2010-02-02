/**
 * 
 */
package com.hunthawk.framework.tapestry.component;

import java.util.List;

import org.apache.tapestry.AbstractComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.valid.IValidationDelegate;

/**
 * @author sunquanzhi
 *
 */
@ComponentClass(allowBody = false, allowInformalParameters = false)
public abstract class ShowValidateError extends AbstractComponent {

	@Parameter(required = true)
    public abstract IValidationDelegate getDelegate();

    @Override
    @SuppressWarnings("unchecked")
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        if (cycle.isRewinding())
            return;

        IValidationDelegate delegate = getDelegate();

        if (!delegate.getHasErrors())
            return;

        writer.begin("table");
        writer.attribute("class", "inputerror");

        writer.begin("tr");
        writer.attribute("valign", "top");

        writer.begin("td");
        writer.beginEmpty("img");
        writer.attribute("src", getWarnicon().buildURL());
        
        writer.attribute("width", 16);
        writer.attribute("height", 16);
        writer.end();

        writer.begin("td");
        writer.attribute("class", "message");

        List<IRender> errorRenders = delegate.getErrorRenderers();

        errorRenders.get(0).render(writer, cycle);

        writer.end("table");
    }

    @Asset("iconWarning.gif")
    public abstract IAsset getWarnicon();
}
