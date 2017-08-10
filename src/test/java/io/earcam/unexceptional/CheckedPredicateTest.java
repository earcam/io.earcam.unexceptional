package io.earcam.unexceptional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;

public class CheckedPredicateTest {

	private static final CheckedPredicate<String> YES = (CheckedPredicate<String> & Serializable) "yes"::equals;


	@Test
	public void and() throws Throwable
	{
		CheckedPredicate<String> b = (CheckedPredicate<String> & Serializable) "yesterday"::startsWith;

		assertThat(YES.and(b).test("yes"), is(true));
	}


	@Test
	public void andFirstFails() throws Throwable
	{
		CheckedPredicate<String> b = (CheckedPredicate<String> & Serializable) "yesterday"::startsWith;

		assertThat(YES.and(b).test("yup"), is(false));
	}


	@Test
	public void andLastFails() throws Throwable
	{
		CheckedPredicate<String> a = (CheckedPredicate<String> & Serializable) "yeah"::equals;
		CheckedPredicate<String> b = (CheckedPredicate<String> & Serializable) "ya know"::startsWith;

		assertThat(a.and(b).test("yeah"), is(false));
	}


	@Test
	public void orFirstSucceeds() throws Throwable
	{
		CheckedPredicate<String> b = (CheckedPredicate<String> & Serializable) "yesterday"::startsWith;

		assertThat(YES.or(b).test("yes"), is(true));
	}


	@Test
	public void orLastSucceeds() throws Throwable
	{
		CheckedPredicate<String> a = (CheckedPredicate<String> & Serializable) "yeah"::equals;
		CheckedPredicate<String> b = (CheckedPredicate<String> & Serializable) "yesterday"::startsWith;

		assertThat(a.or(b).test("yes"), is(true));
	}


	@Test
	public void orBothFail() throws Throwable
	{
		CheckedPredicate<String> a = (CheckedPredicate<String> & Serializable) "nope"::equals;
		CheckedPredicate<String> b = (CheckedPredicate<String> & Serializable) "no"::startsWith;

		assertThat(a.or(b).test("yes"), is(false));
	}


	@Test
	public void theNegatedPredicateFromDelmonteSaysYes() throws Throwable
	{
		CheckedPredicate<String> predicate = (CheckedPredicate<String> & Serializable) "no"::startsWith;

		assertThat(predicate.negate().test("yes"), is(true));
	}


	@Test
	public void theNegatedPredicateFromDelmonteSaysNoooooooo() throws Throwable
	{
		assertThat(YES.negate().test("yes"), is(false));
	}


	@Test
	public void negatePredicateIsSerializable() throws Throwable
	{
		CheckedPredicate<String> negated = YES.negate();

		@SuppressWarnings("unchecked")
		CheckedPredicate<String> deserialized = SerialCodec.deserialize(SerialCodec.serialize(negated), CheckedPredicate.class);

		assertThat(deserialized.test("yes"), is(false));
	}


	@Test
	public void unionPredicateIsSerializable() throws Throwable
	{
		CheckedPredicate<String> s = (CheckedPredicate<String> & Serializable) t -> t.startsWith("ye");
		CheckedPredicate<String> e = (CheckedPredicate<String> & Serializable) t -> t.endsWith("es");

		CheckedPredicate<String> united = YES.and(s).and(e);

		@SuppressWarnings("unchecked")
		CheckedPredicate<String> deserialized = SerialCodec.deserialize(SerialCodec.serialize(united), CheckedPredicate.class);

		assertThat(deserialized.test("yes"), is(true));
	}


	@Test
	public void intersectionPredicateIsSerializable() throws Throwable
	{
		CheckedPredicate<String> p = (CheckedPredicate<String> & Serializable) "yes"::equals;
		CheckedPredicate<String> s = (CheckedPredicate<String> & Serializable) t -> t.startsWith("NO");
		CheckedPredicate<String> e = (CheckedPredicate<String> & Serializable) t -> t.endsWith("es");

		CheckedPredicate<String> united = p.or(s).or(e);

		@SuppressWarnings("unchecked")
		CheckedPredicate<String> deserialized = SerialCodec.deserialize(SerialCodec.serialize(united), CheckedPredicate.class);

		assertThat(deserialized.test("Yes"), is(true));
	}


	@Test
	public void orEagerlyThrowsNullPointerWhenOtherIsNull()
	{
		try {
			YES.or(null);
			fail();
		} catch(NullPointerException npe) {}
	}


	@Test
	public void andEagerlyThrowsNullPointerWhenOtherIsNull()
	{
		try {
			YES.and(null);
			fail();
		} catch(NullPointerException npe) {}
	}
}
