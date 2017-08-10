package io.earcam.unexceptional.example;

//EARCAM_SNIPPET_BEGIN: import

import static io.earcam.unexceptional.Exceptional.apply;
import static io.earcam.unexceptional.Exceptional.uncheckFunction;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

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
