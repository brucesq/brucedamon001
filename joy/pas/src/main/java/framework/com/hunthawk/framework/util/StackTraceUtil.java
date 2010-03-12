/**
 * 
 */
package com.hunthawk.framework.util;

/**
 * @author sunquanzhi
 *
 */
public class StackTraceUtil {
	public static void buildStackTrace(
            String msg,
            StackTraceElement[] stackTraceElements,
            StringBuilder sb,
            int maxDepth)
    {
        sb.append(msg).append(" \n")
          .append("   Started at: \n");
        for (int i = 0; i < stackTraceElements.length; i++)
        {
            if (i > maxDepth && maxDepth > 0)
            {
                sb.append("      ...");
                break;
            }
            sb.append("      ").append(stackTraceElements[i]);
            if (i < stackTraceElements.length - 1)
            {
                sb.append("\n");
            }
        }
    }
}
