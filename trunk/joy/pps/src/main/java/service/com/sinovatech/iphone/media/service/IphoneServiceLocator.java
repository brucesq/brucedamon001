/**
 * IphoneServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sinovatech.iphone.media.service;

public class IphoneServiceLocator extends org.apache.axis.client.Service implements com.sinovatech.iphone.media.service.IphoneService {

    public IphoneServiceLocator() {
    }


    public IphoneServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IphoneServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IphoneServiceHttpPort
    private java.lang.String IphoneServiceHttpPort_address = "http://iphone.wo.com.cn/services/IphoneService";//http://202.106.60.12:8080/services/IphoneService";

    public java.lang.String getIphoneServiceHttpPortAddress() {
        return IphoneServiceHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String IphoneServiceHttpPortWSDDServiceName = "IphoneServiceHttpPort";

    public java.lang.String getIphoneServiceHttpPortWSDDServiceName() {
        return IphoneServiceHttpPortWSDDServiceName;
    }

    public void setIphoneServiceHttpPortWSDDServiceName(java.lang.String name) {
        IphoneServiceHttpPortWSDDServiceName = name;
    }

    public com.sinovatech.iphone.media.service.IphoneServicePortType getIphoneServiceHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IphoneServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIphoneServiceHttpPort(endpoint);
    }

    public com.sinovatech.iphone.media.service.IphoneServicePortType getIphoneServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.sinovatech.iphone.media.service.IphoneServiceHttpBindingStub _stub = new com.sinovatech.iphone.media.service.IphoneServiceHttpBindingStub(portAddress, this);
            _stub.setPortName(getIphoneServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIphoneServiceHttpPortEndpointAddress(java.lang.String address) {
        IphoneServiceHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.sinovatech.iphone.media.service.IphoneServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.sinovatech.iphone.media.service.IphoneServiceHttpBindingStub _stub = new com.sinovatech.iphone.media.service.IphoneServiceHttpBindingStub(new java.net.URL(IphoneServiceHttpPort_address), this);
                _stub.setPortName(getIphoneServiceHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("IphoneServiceHttpPort".equals(inputPortName)) {
            return getIphoneServiceHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.media.iphone.sinovatech.com", "IphoneService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.media.iphone.sinovatech.com", "IphoneServiceHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("IphoneServiceHttpPort".equals(portName)) {
            setIphoneServiceHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
