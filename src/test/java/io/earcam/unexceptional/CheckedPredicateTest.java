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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import org.junit.jupiter.api.Test;

public class CheckedPredicateTest {

	private static final CheckedPredicate<String, Throwable> YES = (CheckedPredicate<String, Throwable> & Serializable) "yes"::equals;


	@Test
	public void and() throws Throwable
	{
		CheckedPredicate<String, Throwable> b = (CheckedPredicate<String, Throwable> & Serializable) "yesterday"::startsWith;

		assertThat(YES.and(b).test("yes"), is(true));
	}


	@Test
	public void andFirstFails() throws Throwable
	{
		CheckedPredicate<String, ?> b = (CheckedPredicate<String, ?> & Serializable) "yesterday"::startsWith;

		assertThat(YES.and(b).test("yup"), is(false));
	}


	@Test
	public void andLastFails() throws Throwable
	{
		CheckedPredicate<String, IOException> a = (CheckedPredicate<String, IOException> & Serializable) "yeah"::equals;
		CheckedPredicate<String, FileNotFoundException> b = (CheckedPredicate<String, FileNotFoundException> & Serializable) "ya know"::startsWith;

		assertThat(a.and(b).test("yeah"), is(false));
	}


	@Test
	public void orFirstSucceeds() throws Throwable
	{
		CheckedPredicate<String, ?> b = (CheckedPredicate<String, ?> & Serializable) "yesterday"::startsWith;

		assertThat(YES.or(b).test("yes"), is(true));
	}


	@Test
	public void orLastSucceeds() throws Throwable
	{
		CheckedPredicate<String, Throwable> a = (CheckedPredicate<String, Throwable> & Serializable) "yeah"::equals;
		CheckedPredicate<String, Throwable> b = (CheckedPredicate<String, Throwable> & Serializable) "yesterday"::startsWith;

		assertThat(a.or(b).test("yes"), is(true));
	}


	@Test
	public void orBothFail() throws Throwable
	{
		CheckedPredicate<String, Throwable> a = (CheckedPredicate<String, Throwable> & Serializable) "nope"::equals;
		CheckedPredicate<String, Throwable> b = (CheckedPredicate<String, Throwable> & Serializable) "no"::startsWith;

		assertThat(a.or(b).test("yes"), is(false));
	}


	@Test
	public void theNegatedPredicateFromDelmonteSaysYes() throws Throwable
	{
		CheckedPredicate<String, Throwable> predicate = (CheckedPredicate<String, Throwable> & Serializable) "no"::startsWith;

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
		CheckedPredicate<String, Throwable> negated = YES.negate();

		@SuppressWarnings("unchecked")
		CheckedPredicate<String, Throwable> deserialized = SerialCodec.deserialize(SerialCodec.serialize(negated), CheckedPredicate.class);

		assertThat(deserialized.test("yes"), is(false));
	}


	@Test
	public void unionPredicateIsSerializable() throws Throwable
	{
		CheckedPredicate<String, Throwable> s = (CheckedPredicate<String, Throwable> & Serializable) t -> t.startsWith("ye");
		CheckedPredicate<String, Throwable> e = (CheckedPredicate<String, Throwable> & Serializable) t -> t.endsWith("es");

		CheckedPredicate<String, Throwable> united = YES.and(s).and(e);

		@SuppressWarnings("unchecked")
		CheckedPredicate<String, Throwable> deserialized = SerialCodec.deserialize(SerialCodec.serialize(united), CheckedPredicate.class);

		assertThat(deserialized.test("yes"), is(true));
	}


	@Test
	public void intersectionPredicateIsSerializable() throws Throwable
	{
		CheckedPredicate<String, Throwable> p = (CheckedPredicate<String, Throwable> & Serializable) "yes"::equals;
		CheckedPredicate<String, Throwable> s = (CheckedPredicate<String, Throwable> & Serializable) t -> t.startsWith("NO");
		CheckedPredicate<String, Throwable> e = (CheckedPredicate<String, Throwable> & Serializable) t -> t.endsWith("es");

		CheckedPredicate<String, Throwable> united = p.or(s).or(e);

		@SuppressWarnings("unchecked")
		CheckedPredicate<String, Throwable> deserialized = SerialCodec.deserialize(SerialCodec.serialize(united), CheckedPredicate.class);

		assertThat(deserialized.test("Yes"), is(true));
	}


	@Test
	public void orEagerlyThrowsNullPointerWhenOtherIsNull()
	{
		try {
			YES.or(null);
			fail("should not reach here");
		} catch(NullPointerException npe) {}
	}


	@Test
	public void andEagerlyThrowsNullPointerWhenOtherIsNull()
	{
		try {
			YES.and(null);
		} catch(NullPointerException npe) {}
	}
}
