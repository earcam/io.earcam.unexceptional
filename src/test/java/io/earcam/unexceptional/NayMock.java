package io.earcam.unexceptional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NayMock implements InvocationHandler {

	
	public static class Invocation {
		
		Object proxy;
		String name;
		Object[] args;

		public Invocation(Object proxy, String name, Object[] args)
		{
			this.proxy = proxy;
			this.name = name;
			this.args = args;
		}
	}
	
	
	List<Invocation> invocations = new ArrayList<>();


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		invocations.add(new Invocation(proxy, method.getName(), args));
		return null;
	}

}
