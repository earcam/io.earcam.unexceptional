package io.earcam.unexceptional.example;

import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class FileListExample {

	@Test
	public void fileListEquivalence()
	{
		Path path = get(".", "target");
		
		List<String> list = listFiles(path);

		assertThat(list, is( equalTo( FileListWithExceptionalExample.listFiles(path) )));
		assertThat(list, is( equalTo( FileListWithEmeticStreamExample.listFiles(path) )));
	}


	// Using vanilla Stream API (in a manner that would distress Dijkstra)
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
}