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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

public class UncheckedExceptionTest {

	@Test
	public void message()
	{
		String message = "message";
		UncheckedException e = new UncheckedException(message);

		assertThat(e.getMessage(), is(message));
		assertThat(e.getCause(), is(nullValue()));
	}


	@Test
	public void cause()
	{
		Throwable cause = new Throwable();
		UncheckedException e = new UncheckedException(cause);

		assertThat(e.getCause(), is(cause));
		assertThat(e.getMessage(), is(Throwable.class.getCanonicalName()));
	}


	@Test
	public void messageAndCause()
	{
		String message = "message";
		Throwable cause = new Throwable();
		UncheckedException e = new UncheckedException(message, cause);

		assertThat(e.getCause(), is(cause));
		assertThat(e.getMessage(), is(message));
	}
}
