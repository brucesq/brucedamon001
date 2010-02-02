package com.hunthawk.framework.util;

import com.hunthawk.framework.annotation.Position;


public class PositionUtil {

	public static Object getArg(Position position,Object[] args)
	{
		Object arg = null;
		switch(position)
		{
		case ARG_1:
			 arg = getArg(0,args);
			 break;
		case ARG_2:
			 arg = getArg(1,args);
			 break;
		case ARG_3:
			 arg = getArg(2,args);
			 break;
		case ARG_4:
			 arg = getArg(3,args);
			 break;
		case ARG_5:
			 arg = getArg(4,args);
			 break;
		case ARG_6:
			 arg = getArg(5,args);
			 break;
		case ARG_7:
			 arg = getArg(6,args);
			 break;
		case ARG_8:
			 arg = getArg(7,args);
			 break;
		case ARG_9:
			 arg = getArg(8,args);
			 break;
		default:
				 break;
		}
		return arg;
	}
	private static Object getArg(int pos,Object[] args)
	{
		if(args.length > pos)
		{
			return args[pos];
		}
		return null;
	}

	
}
