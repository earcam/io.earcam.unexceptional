package io.earcam.unexceptional.example;

import static io.earcam.unexceptional.EmeticStream.emesis;
import static io.earcam.unexceptional.Exceptional.*;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import io.earcam.unexceptional.EmeticStream;
import io.earcam.unexceptional.Exceptional;


/**
 * Example usage and comparable with/out-cases for {@link EmeticStream} 
 */
public class EmeticStreamExample {


	
	/**
	 * Demonstrates {@link Files#list} and subsequent stream operations, with;
	 * 
	 * <ul>
	 * <li>Vanilla {@link Stream} API (admittedly overly exaggerated boiler-plating)</li>
	 * <li>{@link Exceptional} with {@link Stream} API</li>
	 * <li>{@link EmeticStream}</li>
	 * </ul>
	 */
	@Test
	public void fileListEquivalence()
	{
		Path path = get(".", "target");

		
		// Using vanilla Stream API (in a manner that would distress Dijkstra)
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


		// Using static imports from Exceptional
		List<String> b = apply(Files::list, path)
			.map(Path::toFile)
			.map(uncheckFunction(File::getCanonicalFile))
			.map(File::toURI)
			.map(uncheckFunction(URI::toURL))
			.map(Object::toString)
			.collect(toList());


		// Using EmeticStream (less efficient on GC, more efficient on the eye)
		List<String> c = emesis(Files::list, path)
			.map(Path::toFile)
			.map(File::getCanonicalFile)
			.map(File::toURI)
			.map(Object::toString)
			.collect(toList());


		assertThat(a, is( equalTo( b )));
		assertThat(b, is( equalTo( c )));
	}
}
