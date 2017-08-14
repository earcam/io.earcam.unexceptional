/*-
 * #%L
 * io.earcam.unexceptional
 * %%
 * Copyright (C) 2016 - 2017 earcam
 * %%
 * SPDX-License-Identifier: (BSD-3-Clause OR EPL-1.0 OR Apache-2.0 OR MIT)
 * 
 * You <b>must</b> choose to accept, in full - any individual or combination of 
 * the following licenses:
 * <ul>
 * 	<li><a href="https://opensource.org/licenses/BSD-3-Clause">BSD-3-Clause</a></li>
 * 	<li><a href="https://www.eclipse.org/legal/epl-v10.html">EPL-1.0</a></li>
 * 	<li><a href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a></li>
 * 	<li><a href="https://www.opensource.org/licenses/MIT">MIT</a></li>
 * </ul>
 * #L%
 */
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
