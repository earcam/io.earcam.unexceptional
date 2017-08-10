package io.earcam.unexceptional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CheckedBiFunctionTest {

	CheckedBiFunction<Integer, Integer, Double> div = (a, b) -> a / (double) b;
	CheckedFunction<Double, Double> abs = Math::abs;


	@Test
	public void andThenChains() throws Throwable
	{
		assertThat(div.andThen(abs).apply(-10, 2), is(5.0D));
	}


	@Test
	public void andThenEagerlyThrowsNullPointerExceptionWhenAfterIsNull()
	{
		CheckedFunction<Double, String> after = null;
		try {
			div.andThen(after);
			fail();
		} catch(NullPointerException npe) {}
	}
}
