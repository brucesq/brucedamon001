/**
 * 
 */
package com.hunthawk.tag.components;

/**
 * @author sunquanzhi
 *
 */
public class ImageLink extends Link {

	

	public void setImage(Image image)
	{
		setTitle(image.renderComponent());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImageLink link = new ImageLink();
		link.setUrl("http://asss");
		Image img = new Image();
		img.setUrl("http://img");
		
		link.setImage(img);
		System.out.println(link.renderComponent());
	}


}
