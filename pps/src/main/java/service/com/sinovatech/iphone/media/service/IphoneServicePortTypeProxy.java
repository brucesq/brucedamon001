package com.sinovatech.iphone.media.service;

public class IphoneServicePortTypeProxy implements com.sinovatech.iphone.media.service.IphoneServicePortType {
  private String _endpoint = null;
  private com.sinovatech.iphone.media.service.IphoneServicePortType iphoneServicePortType = null;
  
  public IphoneServicePortTypeProxy() {
    _initIphoneServicePortTypeProxy();
  }
  
  public IphoneServicePortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initIphoneServicePortTypeProxy();
  }
  
  private void _initIphoneServicePortTypeProxy() {
    try {
      iphoneServicePortType = (new com.sinovatech.iphone.media.service.IphoneServiceLocator()).getIphoneServiceHttpPort();
      if (iphoneServicePortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iphoneServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iphoneServicePortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iphoneServicePortType != null)
      ((javax.xml.rpc.Stub)iphoneServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.sinovatech.iphone.media.service.IphoneServicePortType getIphoneServicePortType() {
    if (iphoneServicePortType == null)
      _initIphoneServicePortTypeProxy();
    return iphoneServicePortType;
  }
  
  public java.lang.String doService(java.lang.String in0) throws java.rmi.RemoteException{
    if (iphoneServicePortType == null)
      _initIphoneServicePortTypeProxy();
    return iphoneServicePortType.doService(in0);
  }
  
  
}