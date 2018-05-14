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

import static io.earcam.unexceptional.Exceptional.apply;
import static io.earcam.unexceptional.Exceptional.uncheckFunction;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

//EARCAM_SNIPPET_END: import

public class FileListWithExceptionalExample {

	@Test
	public void fileListEquivalence()
	{
		Path path = get(".", "target");

		List<String> list = listFiles(path);

		assertThat(list, is( equalTo( FileListExample.listFiles(path) )));
		assertThat(list, is( equalTo( FileListWithExceptionalExample.listFiles(path) )));
	}


	//EARCAM_SNIPPET_BEGIN: listFiles
	// Using static imports from Exceptional
	static List<String> listFiles(Path path)
	{
		return apply(Files::list, path)
			.map(Path::toFile)
			.map(uncheckFunction(File::getCanonicalFile))
			.map(File::toURI)
			.map(uncheckFunction(URI::toURL))
			.map(Object::toString)
			.collect(toList());
	}
	//EARCAM_SNIPPET_END: listFiles
}
