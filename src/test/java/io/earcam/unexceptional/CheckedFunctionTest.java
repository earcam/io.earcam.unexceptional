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
		assertThat(plusOne.compose(timesTwo).apply(1), is(3));
	}


	@Test
	public void andThenAppliesAfter() throws Throwable
	{
		assertThat(plusOne.andThen(timesTwo).apply(1), is(4));
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
