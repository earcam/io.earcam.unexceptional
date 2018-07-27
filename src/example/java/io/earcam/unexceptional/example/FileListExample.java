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

//EARCAM_SNIPPET_BEGIN: import

import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

//EARCAM_SNIPPET_END: import

public class FileListExample {

	@Test
	public void fileListEquivalence()
	{
		Path path = get(".", "target");
		
		List<String> list = listFiles(path);

		assertThat(list, is( equalTo( FileListWithExceptionalExample.listFiles(path) )));
		assertThat(list, is( equalTo( FileListWithEmeticStreamExample.listFiles(path) )));
	}


	//EARCAM_SNIPPET_BEGIN: listFiles
	// Vanilla Stream API, in a manner that would distress Dijkstra
	public static List<String> listFiles(Path path)
	{
		List<String> a;
		try {
			a = Files.list(path)
					.map(Path::toFile)
					.map(t -> {
						try {
							return t.getCanonicalFile();
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					})
					.map(File::toURI)
					.map(t -> {
						try {
							return t.toURL();
						} catch (MalformedURLException e) {
							throw new UncheckedIOException(e);
						}
					})
					.map(Object::toString)
					.collect(toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return a;
	}
	//EARCAM_SNIPPET_END: listFiles
}
