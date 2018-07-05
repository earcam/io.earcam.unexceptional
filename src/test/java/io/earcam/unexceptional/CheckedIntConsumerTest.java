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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class CheckedIntConsumerTest {

	class NiceStubCheckedIntConsumer implements CheckedIntConsumer {

		int t;


		@Override
		public void accept(int t)
		{
			this.t = t;

		}

	}

	class NastyStubCheckedIntConsumer implements CheckedIntConsumer {

		Exception chuck;


		NastyStubCheckedIntConsumer(Exception chuck)
		{
			this.chuck = chuck;
		}


		@Override
		public void accept(int t) throws Exception
		{
			throw chuck;
		}
	}


	@Test
	public void andThenReturnsAnUncheckedConsumer() throws Throwable
	{
		IOException wobbly = new IOException("boom!");

		CheckedIntConsumer uncheckedConsumer = new NiceStubCheckedIntConsumer().andThen(new NastyStubCheckedIntConsumer(wobbly));

		try {
			uncheckedConsumer.accept(101);
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(sameInstance(wobbly)));
		}
	}


	@Test
	public void andThenRequiresNonNullCheckedConsumer()
	{
		try {
			new NiceStubCheckedIntConsumer().andThen(null);
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(instanceOf(NullPointerException.class)));
		}
	}


	@Test
	public void andThenInvokesAcceptOnBothCheckedIntConsumers() throws Throwable
	{

		NiceStubCheckedIntConsumer a = new NiceStubCheckedIntConsumer();
		NiceStubCheckedIntConsumer b = new NiceStubCheckedIntConsumer();

		int t = 42;
		a.andThen(b).accept(t);

		assertThat(a.t, is(t));
		assertThat(b.t, is(t));
	}


	@Test
	public void andThenInvokesAcceptInOrderWhenLastConsumerThrows() throws Throwable
	{
		int t = 42;
		IOException wobbly = new IOException("goodbye");

		NiceStubCheckedIntConsumer nice = new NiceStubCheckedIntConsumer();
		CheckedIntConsumer nasty = new NastyStubCheckedIntConsumer(wobbly);

		try {
			nice.andThen(nasty).accept(t);
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(sameInstance(wobbly)));
			assertThat(nice.t, is(t));
		}
	}


	@Test
	public void andThenThrowsFirstExceptionWithoutInvokingSubsequentConsumersAccept() throws Throwable
	{
		int t = 101;
		IOException wobbly = new IOException("goodbye");

		CheckedIntConsumer nasty = new NastyStubCheckedIntConsumer(wobbly);
		NiceStubCheckedIntConsumer nice = new NiceStubCheckedIntConsumer();

		try {
			nasty.andThen(nice).accept(t);
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(sameInstance(wobbly)));
			assertThat(nice.t, is(0));
		}
	}

}
