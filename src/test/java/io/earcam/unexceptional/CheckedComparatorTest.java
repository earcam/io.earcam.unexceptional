package io.earcam.unexceptional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

import org.junit.Test;

public class CheckedComparatorTest {


	private static class Compound {
		int a;
		int b;
		
		public int a() throws Throwable
		{
			return a;
		}
		
		public int b() throws Throwable
		{
			return b;
		}
	}


	private static class Compared {
		String s;
		double d;
		long l;
		Compound c;
		Object amorphous;
		
		
		public String s() throws Exception
		{
			return s;
		}
		
		public double d() throws Exception
		{
			return d;
		}
		
		public long l() throws Exception
		{
			return l;
		}

		public Compound c() throws Throwable
		{
			return c;
		}
		
		public String amorphous()
		{
			return amorphous.toString();
		}
	}

	private final CheckedComparator<Compared> comparator = CheckedComparator.comparing(Compared::s)
			.thenComparingDouble(Compared::d)
			.thenComparingLong(Compared::l)
			.thenComparing(Compared::amorphous)
			.thenComparing(Compared::c, CheckedComparator.comparingInt(Compound::a).thenComparingInt(Compound::b));


	@Test
	public void thenComparingGreaterThan()
	{
		Compared a = createCompared(21);
		Compared b = createCompared(12);

		int diff = Exceptional.applyAsInt(comparator::compare, a, b);
		assertThat(diff, is( greaterThan( 0 )));
	}


	@Test
	public void thenComparingArbitraryAmorphous()  //TODO this should be more that a fudge for branch coverage - personal integrity fail, FIXME/FIXYOU
	{
		Compared a = createCompared(21);
		Compared b = createCompared(21);

		b.amorphous = new Object();
		
		int diff = Exceptional.applyAsInt(comparator::compare, a, b);
		assertThat(diff, is( not( equalTo( 0 ))));
	}


	Compared createCompared(int value)
	{
		Compared compared = new Compared();
		compared.s = "s";
		compared.d = 42D;
		compared.l = 101L;
		compared.c = new Compound();
		compared.c.a = 1;
		compared.c.b = value; 
		compared.amorphous = this;
		return compared;
	}


	@Test
	public void thenComparingLessThan()
	{
		Compared a = createCompared(12);
		Compared b = createCompared(21);

		int diff = Exceptional.applyAsInt(comparator::compare, a, b);
		assertThat(diff, is( lessThan( 0 )));
	}


	@Test
	public void thenComparingEqual()
	{
		Compared a = createCompared(21);
		Compared b = createCompared(21);

		int diff = Exceptional.applyAsInt(comparator::compare, a, b);
		assertThat(diff, is( equalTo( 0 )));
	}


	@Test
	public void reverse()
	{
		Compared a = createCompared(12);
		Compared b = createCompared(21);

		int diff = Exceptional.applyAsInt(comparator.reversed()::compare, a, b);
		assertThat(diff, is( greaterThan( 0 )));
	}
}