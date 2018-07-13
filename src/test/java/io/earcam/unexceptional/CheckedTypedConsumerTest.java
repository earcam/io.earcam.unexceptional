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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

public class CheckedTypedConsumerTest {

	@Test
	public void andThenThrowsWhenReceivingNull()
	{
		CheckedTypedConsumer<String, IOException> ok = t -> {};

		try {
			ok.andThen(null);
			fail();
		} catch(NullPointerException e) {}
	}


	@Test
	public void andThenBothAccepted() throws IOException
	{
		AtomicReference<String> a = new AtomicReference<>("");
		CheckedTypedConsumer<String, IOException> aOk = a::set;

		AtomicReference<String> b = new AtomicReference<>("");
		CheckedTypedConsumer<String, IOException> bOk = b::set;

		aOk.andThen(bOk).accept("hello");

		MatcherAssert.assertThat(a.get(), is(equalTo("hello")));
		MatcherAssert.assertThat(b.get(), is(equalTo("hello")));
	}
}
