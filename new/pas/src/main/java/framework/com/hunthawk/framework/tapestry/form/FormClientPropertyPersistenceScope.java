
package com.hunthawk.framework.tapestry.form;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.ServiceEncoding;
import org.apache.tapestry.record.AbstractPrefixedClientPropertyPersistenceScope;
import org.apache.tapestry.record.PersistentPropertyData;

public class FormClientPropertyPersistenceScope extends AbstractPrefixedClientPropertyPersistenceScope {
    private IRequestCycle _requestCycle;

    public FormClientPropertyPersistenceScope() {
        super("form:");
    }

    public boolean shouldEncodeState(ServiceEncoding encoding, String pageName, PersistentPropertyData data) {
        return pageName.equals(_requestCycle.getPage().getPageName());
    }

    public void setRequestCycle(IRequestCycle requestCycle) {
        _requestCycle = requestCycle;
    }

}