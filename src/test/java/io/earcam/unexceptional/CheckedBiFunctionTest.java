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
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CheckedBiFunctionTest {

	CheckedBiFunction<Integer, Integer, Double, Throwable> div = (a, b) -> a / (double) b;
	CheckedFunction<Double, Double, ?> abs = Math::abs;


	@Test
	public void andThenChains() throws Throwable
	{
		assertThat(div.andThen(abs).apply(-10, 2), is(5.0D));
	}


	@Test
	public void andThenEagerlyThrowsNullPointerExceptionWhenAfterIsNull()
	{
		CheckedFunction<Double, String, ?> after = null;
		try {
			div.andThen(after);
			fail("should not reach here");
		} catch(NullPointerException npe) {}
	}
}
