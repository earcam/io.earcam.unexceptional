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

import static io.earcam.unexceptional.SerialCodec.serializable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

public class CheckedBinaryOperatorTest implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final CheckedComparator<String> GOOD = new Meh();

	public static class Meh implements CheckedComparator<String>, Serializable {

		private static final long serialVersionUID = 1L;


		@Override
		public int compare(String o1, String o2) throws Throwable
		{
			return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
		}

	}


	@Test
	public void maxByRequiresNonNull()
	{
		try {
			CheckedBinaryOperator.maxBy(null);
			fail("should not reach here");
		} catch(NullPointerException e) {}
	}


	@Test
	public void minByRequiresNonNull()
	{
		try {
			CheckedBinaryOperator.minBy(null);
			fail("should not reach here");
		} catch(NullPointerException e) {}
	}


	/**
	 * This test exists for the sole purpose of killing PiTest mutation "changed conditional boundary".
	 * 
	 * The mutation is benign, only able to replace less-than with less-than-or-equal.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void minByReturnsSecondWhenEqual() throws Throwable
	{
		CheckedBinaryOperator<Object> minBy = CheckedBinaryOperator.minBy((a, b) -> 0);
		Object a = new Object();
		Object b = new Object();

		Object applied = minBy.apply(a, b);

		assertThat(applied, is(sameInstance(b)));
	}


	/**
	 * This test exists for the sole purpose of killing PiTest mutation "changed conditional boundary".
	 * 
	 * The mutation is benign, only able to replace less-than with less-than-or-equal.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void maxByReturnsSecondWhenEqual() throws Throwable
	{
		CheckedBinaryOperator<Object> maxBy = CheckedBinaryOperator.maxBy((a, b) -> 0);
		Object a = new Object();
		Object b = new Object();

		Object applied = maxBy.apply(a, b);

		assertThat(applied, is(sameInstance(b)));
	}


	@Test
	public void minBy() throws Throwable
	{
		CheckedBinaryOperator<String> minBy = CheckedBinaryOperator.minBy(String.CASE_INSENSITIVE_ORDER::compare);

		assertThat(minBy.apply("a", "b"), is(equalTo("a")));
	}


	@Test
	public void minByArgsReversedToCoverBranching() throws Throwable
	{
		CheckedBinaryOperator<String> minBy = CheckedBinaryOperator.minBy(String.CASE_INSENSITIVE_ORDER::compare);

		assertThat(minBy.apply("b", "a"), is(equalTo("a")));
	}


	@Test
	public void maxBy() throws Throwable
	{
		CheckedBinaryOperator<String> maxBy = CheckedBinaryOperator.maxBy(String.CASE_INSENSITIVE_ORDER::compare);

		assertThat(maxBy.apply("a", "b"), is(equalTo("b")));
	}


	@Test
	public void maxByReversedToCoverBranching() throws Throwable
	{
		CheckedBinaryOperator<String> maxBy = CheckedBinaryOperator.maxBy(String.CASE_INSENSITIVE_ORDER::compare);

		assertThat(maxBy.apply("b", "a"), is(equalTo("b")));
	}


	@Test
	public void serializableIfComparatorIs()
	{
		CheckedBinaryOperator<String> op = CheckedBinaryOperator.maxBy(GOOD);

		assertThat(op, is(serializable()));
	}
}
