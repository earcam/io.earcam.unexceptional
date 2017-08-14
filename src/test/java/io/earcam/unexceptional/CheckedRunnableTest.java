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

import static io.earcam.unexceptional.Exceptional.uncheckRunnable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.Serializable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CheckedRunnableTest {

	private static final RuntimeException EXCEPTION = new RuntimeException("bang");
	private boolean niceWasRun = false;
	private boolean nastyWasRun = false;

	private final CheckedRunnable nice = (CheckedRunnable & Serializable) () -> niceWasRun = true;
	private final CheckedRunnable nasty = (CheckedRunnable & Serializable) () -> {
		nastyWasRun = true;
		throw EXCEPTION;
	};

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Test
	public void isSerializable()
	{
		final CheckedRunnable a = (CheckedRunnable & Serializable) () -> {};
		final CheckedRunnable b = (CheckedRunnable & Serializable) () -> {};

		assertThat(SerialCodec.isSerializable(a.andThen(b)), is(true));
	}


	@Test
	public void invokesSequentially()
	{
		uncheckRunnable(nice.andThen(() -> nastyWasRun = true)).run();
		assertThat(niceWasRun, is(true));
		assertThat(nastyWasRun, is(true));
	}


	@Test
	public void invokesSequentiallyThenThrows()
	{
		thrown.expect(sameInstance(EXCEPTION));
		try {
			uncheckRunnable(nice.andThen(nasty)).run();
		} finally {
			assertThat(niceWasRun, is(true));
			assertThat(nastyWasRun, is(true));
		}
	}


	@Test
	public void thrownExceptionTerminatesEarly()
	{
		thrown.expect(sameInstance(EXCEPTION));
		try {
			uncheckRunnable(nasty.andThen(nice)).run();
		} finally {
			assertThat(niceWasRun, is(false));
			assertThat(nastyWasRun, is(true));
		}
	}


	@Test
	public void andThenEagerlyThrowsNullPointerWhenAfterIsNull()
	{
		try {
			nice.andThen(null);
			fail();
		} catch(NullPointerException npe) {}
	}
}
