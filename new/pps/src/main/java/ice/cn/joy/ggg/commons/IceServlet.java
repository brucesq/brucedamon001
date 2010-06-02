package cn.joy.ggg.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.joy.ggg.api.service.CommentServicePrx;
import cn.joy.ggg.api.service.CommentServicePrxHelper;
import cn.joy.ggg.api.service.CommunityServicePrx;
import cn.joy.ggg.api.service.CommunityServicePrxHelper;
import cn.joy.ggg.api.service.MessageServicePrx;
import cn.joy.ggg.api.service.MessageServicePrxHelper;
import cn.joy.ggg.api.service.RelationServicePrx;
import cn.joy.ggg.api.service.RelationServicePrxHelper;
import cn.joy.ggg.api.service.UACServicePrx;
import cn.joy.ggg.api.service.UACServicePrxHelper;

public class IceServlet extends HttpServlet {

  private static final long serialVersionUID = -4831655695974724575L;

  private static Ice.Communicator ic = null;

  public static CommentServicePrx SERVICE_PRX_COMMENT = null;
  public static CommunityServicePrx SERVICE_PRX_COMMUNITY = null;
  public static MessageServicePrx SERVICE_PRX_MESSAGE = null;
  public static RelationServicePrx SERVICE_PRX_RELATION = null;
  public static UACServicePrx SERVICE_PRX_UAC = null;

  public IceServlet() {
    super();
  }

  public void destroy() {
    iceDestroy();
  }

  public void init() throws ServletException {
    super.init();
    iceInit();
  }
  
  private boolean iceInit() {
    iceDestroy();
    InputStream fis = null;
    boolean result = false;
    try {
      Properties pro = new Properties();
      fis = this.getClass().getClassLoader().getResourceAsStream("ice.properties");
      pro.load(fis);
      String iceListen = pro.getProperty("ice.listen");
      String commentName = pro.getProperty("comment.name");
      String communityName = pro.getProperty("community.name");
      String messageName = pro.getProperty("message.name");
      String relationName = pro.getProperty("relation.name");
      String uacName = pro.getProperty("uac.name");

      ic = Ice.Util.initialize();
      Ice.ObjectPrx commentBase = ic.stringToProxy(commentName + ":" + iceListen);
      IceServlet.SERVICE_PRX_COMMENT = CommentServicePrxHelper.checkedCast(commentBase);
      Ice.ObjectPrx communityBase = ic.stringToProxy(communityName + ":" + iceListen);
      IceServlet.SERVICE_PRX_COMMUNITY = CommunityServicePrxHelper.checkedCast(communityBase);
      Ice.ObjectPrx messageBase = ic.stringToProxy(messageName + ":" + iceListen);
      IceServlet.SERVICE_PRX_MESSAGE = MessageServicePrxHelper.checkedCast(messageBase);
      Ice.ObjectPrx relationBase = ic.stringToProxy(relationName + ":" + iceListen);
      IceServlet.SERVICE_PRX_RELATION = RelationServicePrxHelper.checkedCast(relationBase);
      Ice.ObjectPrx uacBase = ic.stringToProxy(uacName + ":" + iceListen);
      IceServlet.SERVICE_PRX_UAC = UACServicePrxHelper.checkedCast(uacBase);
      result = true;
    }
    catch(Ice.LocalException e) {
      e.printStackTrace();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
      if(fis != null) {
        try {
          fis.close();
        }
        catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  private void iceDestroy() {
    if(ic != null) {
      try {
        ic.destroy();
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
    PrintWriter out = response.getWriter();
    if(iceInit()) {
      out.print("ice client init success!");
    }
    else {
      out.print("ice client init failure, please try again.");
    }
    out.close();
  }
}
