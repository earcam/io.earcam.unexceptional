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

import static io.earcam.unexceptional.Closing.closeAfterApplying;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

import org.junit.Test;

public class FreePortFinderExample {

	@Test
	public void test()
	{
		assertThat(findFreePort(), is(not(equalTo(0))));

		assertThat(findFreePortVanilla(), is(not(equalTo(0))));
	}
	
	
	// EARCAM_SNIPPET_BEGIN: vanilla
	public static int findFreePortVanilla()
	{
		try(ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	// EARCAM_SNIPPET_END: vanilla


	// EARCAM_SNIPPET_BEGIN: with
	public static int findFreePort()
	{
		return closeAfterApplying(ServerSocket::new, 0, ServerSocket::getLocalPort);
	}
	// EARCAM_SNIPPET_END: with
}
