/**
 * 
 */
package com.hunthawk.framework.tapestry.translator;

/**
 * @author sunquanzhi
 *
 */

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.translator.FormatTranslator;
import org.apache.tapestry.json.JSONLiteral;
import org.apache.tapestry.json.JSONObject;
import org.apache.tapestry.valid.ValidationConstants;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationStrings;


public class NumberTranslator extends FormatTranslator
{
    private boolean _omitZero = false;

    public NumberTranslator()
    {
    }

    //TODO: Needed until HIVEMIND-134 fix is available
    public NumberTranslator(String initializer)
    {
        PropertyUtils.configureProperties(this, initializer);
    }
    
    protected String formatObject(IFormComponent field, Locale locale, Object object)
    {
        Number number = (Number) object;

        if (_omitZero)
        {
            if (number.doubleValue() == 0)
                return "";
        }
        
        return super.formatObject(field, locale, object);
    }
    
    protected Object getValueForEmptyInput()
    {
        return new Double(0);
    }
    
    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#defaultPattern()
     */
    protected String defaultPattern()
    {
        return "#";
    }

    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#getFormat(java.util.Locale)
     */
    protected Format getFormat(Locale locale)
    {
        return getDecimalFormat(locale);
    }

    public DecimalFormat getDecimalFormat(Locale locale)
    {
        return new DecimalFormat(getPattern(), new DecimalFormatSymbols(locale));
    }

    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#getMessageKey()
     */
    protected String getMessageKey()
    {
        return ValidationStrings.INVALID_NUMBER;
    }

    /**
     * @see org.apache.tapestry.form.translator.AbstractTranslator#getMessageParameters(java.util.Locale,
     *      java.lang.String)
     */
    protected Object[] getMessageParameters(Locale locale, String label)
    {
        String pattern = getDecimalFormat(locale).toLocalizedPattern();
        
        return new Object[] { label, pattern };
    }

    /**
     * @see org.apache.tapestry.form.FormComponentContributor#renderContribution(org.apache.tapestry.IMarkupWriter,
     *      org.apache.tapestry.IRequestCycle, FormComponentContributorContext,
     *      org.apache.tapestry.form.IFormComponent)
     */
    public void renderContribution(IMarkupWriter writer, IRequestCycle cycle,
            FormComponentContributorContext context, IFormComponent field)
    {
        super.renderContribution(writer, cycle, context, field);
        
        String message = buildMessage(context, field, getMessageKey());
        
        JSONObject profile = context.getProfile();
        if (!profile.has(ValidationConstants.CONSTRAINTS)) {
            profile.put(ValidationConstants.CONSTRAINTS, new JSONObject());
        }
        
        JSONObject cons = profile.getJSONObject(ValidationConstants.CONSTRAINTS);
        
        DecimalFormat format = getDecimalFormat(context.getLocale());
        
        cons.accumulate(field.getClientId(),
                new JSONLiteral("[dojo.validate.isRealNumber,{"
                        + "places:" + format.getMaximumFractionDigits() + ","
                        + "decimal:" 
                        + JSONObject.quote(format.getDecimalFormatSymbols().getDecimalSeparator()) 
                        //+ ",separator:" //+ JSONObject.quote(format.getDecimalFormatSymbols().getGroupingSeparator())
                        + "}]"));
        
        accumulateProfileProperty(field, profile, ValidationConstants.CONSTRAINTS, message);
    }

    /**
     * @see org.apache.tapestry.form.translator.FormatTranslator#getConstraint()
     */
    protected ValidationConstraint getConstraint()
    {
        return ValidationConstraint.NUMBER_FORMAT;
    }

    /**
     * If true (which is the default for the property), then values that are 0 are rendered to an
     * empty string, not "0" or "0.00". This is useful in most cases where the field is optional; it
     * allows the field to render blank when no value is present.
     */

    public void setOmitZero(boolean omitZero)
    {
        _omitZero = omitZero;
    }

}
