package io.earcam.unexceptional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CheckedConsumerTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	class NiceStubCheckedConsumer implements CheckedConsumer<String> {

		String t;


		@Override
		public void accept(String t)
		{
			this.t = t;

		}

	}

	class NastyStubCheckedConsumer implements CheckedConsumer<String> {

		Exception chuck;


		NastyStubCheckedConsumer(Exception chuck)
		{
			this.chuck = chuck;
		}


		@Override
		public void accept(String t) throws Exception
		{
			throw chuck;
		}
	}


	@Test
	public void andThenReturnsAnUncheckedConsumer() throws Throwable
	{
		IOException wobbly = new IOException("boom!");

		expectedException.expect(sameInstance(wobbly));

		CheckedConsumer<String> uncheckedConsumer = new NiceStubCheckedConsumer().andThen(new NastyStubCheckedConsumer(wobbly));

		uncheckedConsumer.accept("no chance");
	}


	@Test
	public void andThenRequiresNonNullCheckedConsumer()
	{
		expectedException.expect(NullPointerException.class);

		new NiceStubCheckedConsumer().andThen(null);
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
		IOException wobbly = new IOException("goodbye");
		expectedException.expect(sameInstance(wobbly));

		NiceStubCheckedConsumer nice = new NiceStubCheckedConsumer();
		CheckedConsumer<String> nasty = new NastyStubCheckedConsumer(wobbly);

		try {
			nice.andThen(nasty).accept(t);
		} finally {
			assertThat(nice.t, is(t));
		}
	}


	@Test
	public void andThenThrowsFirstExceptionWithoutInvokingSubsequentConsumersAccept() throws Throwable
	{
		String t = "hello";
		IOException wobbly = new IOException("goodbye");
		expectedException.expect(sameInstance(wobbly));

		CheckedConsumer<String> nasty = new NastyStubCheckedConsumer(wobbly);
		NiceStubCheckedConsumer nice = new NiceStubCheckedConsumer();

		try {
			nasty.andThen(nice).accept(t);
		} finally {
			assertThat(nice.t, is(nullValue()));
		}
	}
}
