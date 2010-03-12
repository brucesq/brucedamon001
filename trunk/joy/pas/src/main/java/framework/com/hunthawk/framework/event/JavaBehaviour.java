/**
 * 
 */
package com.hunthawk.framework.event;

import java.lang.reflect.Method;

/**
 * @author sunquanzhi
 * 
 */
public class JavaBehaviour {

	Object object;
	Method method;
	EventListener.Position[] positions;

	public JavaBehaviour(Object object, Method method,
			EventListener.Position[] positions) {
		this.object = object;
		this.method = method;
		this.positions = positions;
	}

	public void invoke(Object[] args, Object return_obj) {
		try {
			Object[] _args = new Object[positions.length];
			for (int i = 0; i < positions.length; i++) {
				EventListener.Position position = positions[i];
				if (position == EventListener.Position.RETURN_OBJ) {
					_args[i] = return_obj;
				} else {
					_args[i] = getArg(position, args);
				}
			}
			method.invoke(object, _args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object getArg(EventListener.Position position, Object[] args) {
		Object arg = null;
		switch (position) {
		case ARG_1:
			arg = getArg(0, args);
			break;
		case ARG_2:
			arg = getArg(1, args);
			break;
		case ARG_3:
			arg = getArg(2, args);
			break;
		case ARG_4:
			arg = getArg(3, args);
			break;
		case ARG_5:
			arg = getArg(4, args);
			break;
		case ARG_6:
			arg = getArg(5, args);
			break;
		case ARG_7:
			arg = getArg(6, args);
			break;
		case ARG_8:
			arg = getArg(7, args);
			break;
		case ARG_9:
			arg = getArg(8, args);
			break;
		default:
			break;
		}
		return arg;
	}

	private Object getArg(int pos, Object[] args) {
		if (args.length > pos) {
			return args[pos];
		}
		return null;
	}
}
