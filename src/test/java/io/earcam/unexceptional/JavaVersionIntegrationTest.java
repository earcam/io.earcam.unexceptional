/*-
 * #%L
 * io.earcam.unexceptional
 * %%
 * Copyright (C) 2016 - 2018 earcam
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
 */package io.earcam.unexceptional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class JavaVersionIntegrationTest {

	private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
	private static final int DEFAULT_BUFFER_SIZE = 8192;

	private static final byte[] CAFEBABE = { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE };


	@Test
	public void ensureClassesAreTargetedForJavaVersionEight() throws IOException
	{
		int classFileMajorVersion = 52;
		assertJavaMajorVersion(Exceptional.class, classFileMajorVersion);
		assertJavaMajorVersion(Closing.class, classFileMajorVersion);
		assertJavaMajorVersion(UncheckedException.class, classFileMajorVersion);
		assertJavaMajorVersion(EmeticStream.class, classFileMajorVersion);
	}


	private void assertJavaMajorVersion(Class<?> type, int classFileMajorVersion) throws IOException
	{
		String name = type.getCanonicalName().replace('.', '/') + ".class";
		ClassLoader loader = type.getClassLoader();

		try(InputStream inputStream = loader.getResourceAsStream(name)) {
			byte[] bytes = readAllBytes(inputStream);

			assertMagicNumber(bytes);
			assertTargetVersion(bytes, classFileMajorVersion);
		}
	}


	private static void assertMagicNumber(byte[] compiled)
	{
		assertThat(compiled[0], is(CAFEBABE[0]));
		assertThat(compiled[1], is(CAFEBABE[1]));
		assertThat(compiled[2], is(CAFEBABE[2]));
		assertThat(compiled[3], is(CAFEBABE[3]));
	}


	private void assertTargetVersion(byte[] compiled, int classFileMajorVersion)
	{
		int inClassFile = compiled[7];
		assertThat(inClassFile, is(classFileMajorVersion));
	}


	private static byte[] readAllBytes(InputStream in) throws IOException
	{
		byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
		int capacity = buf.length;
		int nread = 0;
		int n;
		for(;;) {
			// read to EOF which may read more or less than initial buffer size
			while((n = in.read(buf, nread, capacity - nread)) > 0)
				nread += n;

			// if the last call to read returned -1, then we're done
			if(n < 0)
				break;

			// need to allocate a larger buffer
			if(capacity <= MAX_BUFFER_SIZE - capacity) {
				capacity = capacity << 1;
			} else {
				if(capacity == MAX_BUFFER_SIZE)
					throw new OutOfMemoryError("Required array size too large");
				capacity = MAX_BUFFER_SIZE;
			}
			buf = Arrays.copyOf(buf, capacity);
		}
		return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
	}
}
