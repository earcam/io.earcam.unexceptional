package io.earcam.unexceptional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CheckedFunctionTest {

	
	private final CheckedFunction<Integer, Integer> plusOne = t -> t + 1;
	private final CheckedFunction<Integer, Integer> timesTwo = t -> t * 2;


	@Test
	public void composeAppliesBefore() throws Throwable
	{
		assertThat(plusOne.compose(timesTwo).apply(1), is( 3 ));
	}


	@Test
	public void andThenAppliesAfter() throws Throwable
	{
		assertThat(plusOne.andThen(timesTwo).apply(1), is( 4 ));
	}


	@Test
	public void andThenEagerlyThrowsNullPointerWhenAfterIsNull()
	{
		try {
			plusOne.andThen(null);
			fail();
		} catch(NullPointerException npe) {}		
	}


	@Test
	public void composeEagerlyThrowsNullPointerWhenBeforeIsNull()
	{
		try {
			plusOne.compose(null);
			fail();
		} catch(NullPointerException npe) {}		
	}
}
