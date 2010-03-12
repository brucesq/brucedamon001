package com.hunthawk.framework.tapestry.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.web.WebSession;


import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class ValidatorImageService implements IEngineService 
{
	
	Logger logger=Logger.getLogger(ValidatorImageService.class);  
    
    private HttpServletResponse response;   
    private ServletContext context;   
    private String TYPE="jpeg";   
  
    public void setResponse(HttpServletResponse response){   
        this.response = response;   
    }   
       
    public void setContext(ServletContext context){   
        this.context=context;   
    }   
  
    public ILink getLink(boolean arg0, Object arg1) {   
        return null;   
    }   
  
    public String getName() {   
        return "validatorImage";   
    }   
  
    public void service(IRequestCycle cycle) throws IOException {   
        Random random = new Random();   
        StringBuffer sb=new StringBuffer();   
        for(int i=0;i<4;i++){   
            int x=random.nextInt(25);   
            x+=65;   
            sb.append(String.valueOf((char)x));   
            //sb.append(" ");   
        }          
        String validateString=sb.toString();           
        WebSession sess=cycle.getInfrastructure().getRequest().getSession(true);   
        sess.setAttribute("img_code", validateString);   
       
        String code = (String) sess.getAttribute("img_code");
        
        
        //画布大小   
        int width = 60, height = 20;   
        BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);   
        Graphics g = image.getGraphics();   
        // 以下填充背景颜色   
        g.setColor(Color.decode("#FFFFFF"));   
        g.fillRect(0, 0, width, height);   
        //随机产生其他元素，使图象中的认证码不易被其它程序探测到   
        g.setColor(getRandColor(160, 200));   
        for (int i = 0; i < 300; i++) {   
            int x = random.nextInt(width);   
            int y = random.nextInt(height);   
            int xl = random.nextInt(12);   
            int yl = random.nextInt(12);   
            g.drawLine(x, y, x + xl, y + yl);   
        }   
           
        //字的颜色   
        g.setColor(Color.decode("#01556B"));   
        //写的字的大小   
        g.setFont(new Font(null, Font.BOLD, 18));   
        //在画布上写字   
        g.drawString(validateString, 2, 16);   
        g.dispose();   
        OutputStream os = response.getOutputStream();   
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);   
        encoder.encode(image);   
        os.close();   
    }   
       
    private Color getRandColor(int fc,int bc){//给定范围获得随机颜色   
        Random random = new Random();   
        if(fc>255) fc=255;   
        if(bc>255) bc=255;   
        int r=fc+random.nextInt(bc-fc);   
        int g=fc+random.nextInt(bc-fc);   
        int b=fc+random.nextInt(bc-fc);   
        return new Color(r,g,b);   
    }   


}
