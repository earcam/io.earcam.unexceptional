package io.earcam.unexceptional;

import static io.earcam.unexceptional.SerialCodec.serializable;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;

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
			fail();
		} catch(NullPointerException e) {}
	}


	@Test
	public void minByRequiresNonNull()
	{
		try {
			CheckedBinaryOperator.minBy(null);
			fail();
		} catch(NullPointerException e) {}
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
