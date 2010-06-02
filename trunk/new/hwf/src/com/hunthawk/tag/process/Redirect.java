/**
 * 
 */
package com.hunthawk.tag.process;

/**
 * @author sunquanzhi
 * 
 */
public class Redirect {

	private String gotourl = "";
	private String gototext = "";
	private int time = 5;
	private String title = "";
	// 0 ontimer 1 reponse 2 forward
	private int mode = 0;

	public void setGotourl(String url) {
		this.gotourl = url;
	}

	public void setGotext(String text) {
		this.gototext = text;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getGotourl() {
		return this.gotourl;
	}

	public String getGototext() {
		return this.gototext;
	}

	public int getTime() {
		return this.time;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return this.mode;
	}

	public void sendRedirect() {
		RedirectUtil.sendRedirect(this);
	}

	public void redirect(String url, String title, String text) {
		this.setGotourl(url);
		this.setGotext(text);
		this.setTitle(title);
		RedirectUtil.sendRedirect(this);
	}

	public void redirect(String url, String title, String text, int time) {
		this.setGotourl(url);
		this.setGotext(text);
		this.setTitle(title);
		this.setTime(time);
		RedirectUtil.sendRedirect(this);
	}

	public static void sendRedirect(String url) {
		Redirect redirect = new Redirect();
		redirect.setMode(1);
		redirect.setGotourl(url);
		RedirectUtil.sendRedirect(redirect);
	}

	public static void sendRedirect(String url, String title, String text,
			int time) {
		Redirect redirect = new Redirect();
		redirect.redirect(url, title, text, time);
	}

	public static void sendRedirect(String url, String title, String text) {
		Redirect redirect = new Redirect();
		redirect.redirect(url, title, text);
	}

	public static void sendForward(String url, String title, String text) {
		Redirect redirect = new Redirect();
		redirect.setMode(2);
		redirect.redirect(url, title, text);
	}

}
