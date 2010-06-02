/**
 * 
 */
package com.hunthawk.tag.process;


/**
 * <p>Redirect Util</p>
 * @since 1.1
 * @author sunquanzhi
 *
 */
public class RedirectUtil {

	private static ThreadLocal<Redirect> contain = new ThreadLocal<Redirect>();
	public static void sendRedirect(Redirect redirect)
	{
		contain.set(redirect);
	}

	public static Redirect getRedirect()
	{
		Object obj = contain.get();
		Redirect redirect = null;
		if(obj != null)
		{
			redirect = (Redirect)obj;
		}
		return redirect;
	}
	public static boolean isRedirect()
	{
		Redirect redirect = getRedirect();
		
		if(redirect != null)
		{
			return true;
		}else{
			return false;
		}
	}
	public static void clear()
	{
		contain.remove();
	}
}
