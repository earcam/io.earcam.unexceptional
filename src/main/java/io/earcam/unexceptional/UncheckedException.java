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
 * 	<li><a href="https://opensource.org/licenses/MIT">MIT</a></li>
 * </ul>
 * #L%
 */
package io.earcam.unexceptional;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * A general purpose unchecked exception, should probably be abstract, so use sparingly and only when lazy
 * Prefer the more specific subclasses
 */
@ParametersAreNullableByDefault
public class UncheckedException extends RuntimeException {

	private static final long serialVersionUID = -8070649283076874939L;


	/**
	 * see {@link RuntimeException#RuntimeException(Throwable)}
	 * 
	 * @param cause the underlying cause
	 */
	public UncheckedException(Throwable cause)
	{
		super(cause);
	}


	/**
	 * see {@link RuntimeException#RuntimeException(String)}
	 * 
	 * @param message the detail message
	 */
	public UncheckedException(String message)
	{
		super(message);
	}


	/**
	 * see {@link RuntimeException#RuntimeException(String, Throwable)}
	 * 
	 * @param message the detail message
	 * @param cause the underlying cause
	 */
	public UncheckedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
