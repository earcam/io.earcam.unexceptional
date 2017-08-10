package io.earcam.unexceptional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

public class CheckedBiConsumerTest {

	public class NiceBiConsumer implements CheckedBiConsumer<String, Integer> {

		private String t;
		private Integer u;


		@Override
		public void accept(String t, Integer u) throws Throwable
		{
			this.t = t;
			this.u = u;
		}


		public String getT()
		{
			return t;
		}


		public Integer getU()
		{
			return u;
		}
	}

	public class NastyBiConsumer extends NiceBiConsumer {

		private Exception exception;


		NastyBiConsumer(Exception exception)
		{
			this.exception = exception;

		}


		@Override
		public void accept(String t, Integer u) throws Throwable
		{
			super.accept(t, u);
			throw exception;
		}
	}


	@Test
	public void andThenInvokesSequentially() throws Throwable
	{
		NiceBiConsumer a = new NiceBiConsumer();
		NiceBiConsumer b = new NiceBiConsumer();
		NiceBiConsumer c = new NiceBiConsumer();

		String tea = "tea";
		int forTwo = 42;
		a.andThen(b).andThen(c).accept(tea, forTwo);

		assertThat(a, consumed(tea, forTwo));
		assertThat(b, consumed(tea, forTwo));
		assertThat(c, consumed(tea, forTwo));
	}


	private static Matcher<CheckedBiConsumer<String, Integer>> consumed(String t, Integer u)
	{
		return Matchers.allOf(
				Matchers.hasProperty("t", is(equalTo(t))),
				Matchers.hasProperty("u", is(equalTo(u))));
	}


	@Test
	public void andThenShortcircuitsOnFirstCheckedExceptionRaised() throws Throwable
	{
		IOException chuck = new IOException();
		NiceBiConsumer nice = new NiceBiConsumer();
		NastyBiConsumer nasty = new NastyBiConsumer(chuck);
		NiceBiConsumer notCalled = new NiceBiConsumer();

		String t = "one-oh-one";
		int u = 101;

		try {
			nice.andThen(nasty).andThen(notCalled).accept(t, u);
			fail();
		} catch(Throwable e) {
			assertThat(e, sameInstance(chuck));
		} finally {
			assertThat(nice, consumed(t, u));
			assertThat(nasty, consumed(t, u));
			assertThat(notCalled, consumed(null, null));
		}
	}


	@Test
	public void andThenEagerlyThrowsNullPointerWhenAfterIsNull()
	{
		CheckedBiConsumer<? super String, ? super Integer> after = null;
		try {
			new NiceBiConsumer().andThen(after);
			fail();
		} catch(NullPointerException npe) {}
	}
}
