/**
 * 
 */
package com.hunthawk.framework.security;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.util.BeanUtils;

/**
 * @author sunquanzhi
 * 
 */
@SuppressWarnings("unchecked")
public class SecurityComponentImpl implements SecurityComponent {

	private Identity identity;

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public boolean hasRole(String[] roles) {
		boolean bPermission = false;
		for (String role : roles) {
			if (identity.hasRole(role)) {
				bPermission = true;
				break;
			}
		}

		return bPermission;

	}

	public boolean hasPermission(String name, String action, Object... args) {
		PermissionCheck permissionCheck = new PermissionCheck(name, action);
		return identity.hasPermission(permissionCheck, args);
	}

	public boolean hasPermission(Restrict restrict, Object owner) {
		PermissionCheck permissionCheck = new PermissionCheck(restrict.name(),
				restrict.action());
		if (restrict.mode() == Restrict.Mode.ALL
				|| restrict.mode() == Restrict.Mode.ROLE) {
			if (!hasRole(restrict.roles())) {
				return false;
			}
		}
		if (restrict.mode() == Restrict.Mode.ALL
				|| restrict.mode() == Restrict.Mode.PERMISSION) {
			return identity.hasPermission(permissionCheck, createArgs(restrict,
					new Object[0], owner));
		}
		return true;
	}

	public boolean hasPermission(Restrict restrict, Method method,
			Object[] args, Object owner) {
		String name = restrict.name().equals("") ? method.getClass().getName()
				: restrict.name();
		String action = restrict.action().equals("") ? method.getName()
				: restrict.action();
		PermissionCheck permissionCheck = new PermissionCheck(name, action);

		if (restrict.mode() == Restrict.Mode.ALL
				|| restrict.mode() == Restrict.Mode.ROLE) {
			if (!hasRole(restrict.roles())) {
				return false;
			}
		}
		if (restrict.mode() == Restrict.Mode.ALL
				|| restrict.mode() == Restrict.Mode.PERMISSION) {
			return identity.hasPermission(permissionCheck, createArgs(restrict,
					args, owner));
		}
		return true;
	}

	private Object[] createArgs(Restrict restrict, Object[] args, Object owner) {
		List list = new ArrayList();
		for (String property : restrict.properties()) {
			Object value = null;
			try {
				value = BeanUtils.forceGetProperty(owner, property);// BeanUtils.getProperty(owner,
																	// property);

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (value != null) {
				list.add(value);
			}

		}
		for (Restrict.Position position : restrict.args()) {
			Object arg = getArg(position, args);
			if (arg != null) {
				list.add(arg);
			}
		}
		return list.toArray();
	}

	private Object getArg(Restrict.Position position, Object[] args) {
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
