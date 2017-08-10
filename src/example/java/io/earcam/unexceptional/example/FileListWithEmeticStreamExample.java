package io.earcam.unexceptional.example;

//EARCAM_SNIPPET_BEGIN: import

import static io.earcam.unexceptional.EmeticStream.emesis;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

//EARCAM_SNIPPET_END: import

public class FileListWithEmeticStreamExample {

	@Test
	public void fileListEquivalence()
	{
		Path path = get(".", "target");

		List<String> list = listFiles(path);

		assertThat(list, is( equalTo( FileListExample.listFiles(path) )));
		assertThat(list, is( equalTo( FileListWithExceptionalExample.listFiles(path) )));
	}


	//EARCAM_SNIPPET_BEGIN: listFiles
	//Using EmeticStream (less efficient on GC, more efficient on the eye)
	static List<String> listFiles(Path path)
	{
		return emesis(Files::list, path)
			.map(Path::toFile)
			.map(File::getCanonicalFile)
			.map(File::toURI)
			.map(Object::toString)
			.collect(toList());
	}
	//EARCAM_SNIPPET_END: listFiles
}