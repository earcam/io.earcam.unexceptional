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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class CheckedConsumerTest {

	class NiceStubCheckedConsumer implements CheckedConsumer<String, IOException> {

		String t;


		@Override
		public void accept(String t)
		{
			this.t = t;

		}

	}

	class NastyStubCheckedConsumer<E extends IOException> implements CheckedConsumer<String, E> {

		E chuck;


		NastyStubCheckedConsumer(E chuck)
		{
			this.chuck = chuck;
		}


		@Override
		public void accept(String t) throws E
		{
			throw chuck;
		}
	}


	@Test
	public void andThenReturnsAnUncheckedConsumer() throws Throwable
	{
		FileNotFoundException wobbly = new FileNotFoundException("boom!");

		CheckedConsumer<String, IOException> uncheckedConsumer = new NiceStubCheckedConsumer().andThen(new NastyStubCheckedConsumer<>(wobbly));

		try {
			uncheckedConsumer.accept("no chance");
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(sameInstance(wobbly)));
		}
	}


	@Test
	public void andThenRequiresNonNullCheckedConsumer()
	{
		try {
			new NiceStubCheckedConsumer().andThen(null);
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(instanceOf(NullPointerException.class)));
		}
	}


	@Test
	public void andThenInvokesAcceptOnBothCheckedConsumer() throws Throwable
	{

		NiceStubCheckedConsumer a = new NiceStubCheckedConsumer();
		NiceStubCheckedConsumer b = new NiceStubCheckedConsumer();

		String t = "hello";
		a.andThen(b).accept(t);

		assertThat(a.t, is(t));
		assertThat(b.t, is(t));
	}


	@Test
	public void andThenInvokesAcceptInOrderWhenLastConsumerThrows() throws Throwable
	{
		String t = "hello";
		FileNotFoundException wobbly = new FileNotFoundException("goodbye");

		NiceStubCheckedConsumer nice = new NiceStubCheckedConsumer();
		CheckedConsumer<String, IOException> nasty = new NastyStubCheckedConsumer<>(wobbly);

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
		String t = "hello";
		IOException wobbly = new FileNotFoundException("goodbye");

		CheckedConsumer<String, IOException> nasty = new NastyStubCheckedConsumer<>(wobbly);
		NiceStubCheckedConsumer nice = new NiceStubCheckedConsumer();

		try {
			nasty.andThen(nice).accept(t);
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(sameInstance(wobbly)));
			assertThat(nice.t, is(nullValue()));
		}
	}
}
