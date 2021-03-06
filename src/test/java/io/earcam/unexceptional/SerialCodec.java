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

import static io.earcam.unexceptional.Closing.closeAfterAccepting;
import static io.earcam.unexceptional.Closing.closeAfterApplying;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

final class SerialCodec {

	private SerialCodec()
	{}


	// EARCAM_SNIPPET_BEGIN: serialize
	public static byte[] serialize(Object object)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		closeAfterAccepting(ObjectOutputStream::new, baos, object, ObjectOutputStream::writeObject);
		return baos.toByteArray();
	}
	// EARCAM_SNIPPET_END: serialize


	// EARCAM_SNIPPET_BEGIN: deserialize
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] serialized, Class<T> type)
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		return (T) closeAfterApplying(ObjectInputStream::new, bais, ObjectInputStream::readObject);
	}
	// EARCAM_SNIPPET_END: deserialize


	public static boolean isSerializable(Object instance)
	{
		return deserialize(serialize(instance), instance.getClass()) != null;
	}


	public static Matcher<Object> serializable()
	{
		return new TypeSafeMatcher<Object>() {

			@Override
			public void describeTo(Description description)
			{
				description.appendText(" serializable");
			}


			@Override
			protected boolean matchesSafely(Object item)
			{
				if(item == null) {
					return false;
				}
				return isSerializable(item);
			}
		};
	}
}
