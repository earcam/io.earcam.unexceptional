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
package io.earcam.unexceptional.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

import io.earcam.unexceptional.CheckedPredicate;

public class BoilerplateSerialCodecExample {

	// EARCAM_SNIPPET_BEGIN: serialize
	public static byte[] serialize(Object object)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
			return baos.toByteArray();
		} catch(IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}
	// EARCAM_SNIPPET_END: serialize


	// EARCAM_SNIPPET_BEGIN: deserialize
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] serialized, Class<T> type)
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		try(ObjectInputStream ois = new ObjectInputStream(bais)) {
			return (T) ois.readObject();
		} catch(IOException ioe) {
			throw new UncheckedIOException(ioe);
		} catch(ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		}
	}
	// EARCAM_SNIPPET_END: deserialize


	@SuppressWarnings("unchecked")
	@Test
	public void symetric() throws Throwable
	{
		CheckedPredicate<String> p = (CheckedPredicate<String> & Serializable) "yes"::equals;

		CheckedPredicate<String> phydrated = deserialize(serialize(p), p.getClass());

		assertThat(phydrated.test("yes"), is(true));
	}
}
