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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.ToIntFunction;

import org.junit.jupiter.api.Test;

import io.earcam.unexceptional.Exceptional;


/**
 * In the tests below, {@code a} is equivalent to {@code b} - the notable difference being the lack of try-catch wrapping
 */
public class ExceptionalExample {

	
	public static class SomeArbitraryRuntimeException extends RuntimeException {
		private static final long serialVersionUID = -169337495070938693L;

		public SomeArbitraryRuntimeException(Throwable cause)
		{
			super(cause);
		}
	}
	
	
	private static int parse(String text) throws IOException
	{
		try {
			return Integer.parseInt(text);
		} catch(NumberFormatException nfe) {
			throw new IOException(nfe);
		}
	}
	
	
	
	@Test
	public void convertFunctional()
	{
		//EARCAM_SNIPPET_BEGIN: convertFunctional_without
		ToIntFunction<String> a = value -> {
			try {
				return parse(value);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		//EARCAM_SNIPPET_END: convertFunctional_without

		//EARCAM_SNIPPET_BEGIN: convertFunctional_exceptional
		ToIntFunction<String> b = Exceptional.uncheckToIntFunction(ExceptionalExample::parse);
		//EARCAM_SNIPPET_END: convertFunctional_exceptional

		assertThat(a.applyAsInt("42"), is( equalTo( b.applyAsInt("42") )));
	}
	
	
	
	@Test
	public void invokeFunctional()
	{
		ToIntFunction<String> fn = value -> {
			try {
				return ExceptionalExample.parse(value);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		int a = fn.applyAsInt("42");


		int b = Exceptional.applyAsInt(ExceptionalExample::parse, "42");


		assertThat(a, is( equalTo( b )));
	}	
	
	
	
	
	@Test
	public void uri()
	{
		String ʊri = "http://AchTeaTeaPea.Co.lon/ForwardSlash/ForwardSlash/Valid/Yuri";
		
		URI a = toUriVanilla(ʊri);
		URI b = toUri(ʊri);
		
		assertThat(a, is( equalTo( b )));
	}


	// EARCAM_SNIPPET_BEGIN: uri_vanilla
	public static URI toUriVanilla(String ʊri)
	{
		try {
			return new URI(ʊri);
		} catch (URISyntaxException e) {
			throw new SomeArbitraryRuntimeException(e);
		}
	}
	// EARCAM_SNIPPET_END: uri_vanilla


	// EARCAM_SNIPPET_BEGIN: uri_unexceptional
	public static URI toUri(String ʊri)
	{
		return Exceptional.uri(ʊri);
	}
	// EARCAM_SNIPPET_END: uri_unexceptional
	
	
	
	@Test
	public void applyFilesList()
	{
		Path targetDir = Paths.get(".", "target");

		List<Path> a = fileListVanilla(targetDir);
		List<Path> b = fileList(targetDir);

		assertThat(a, is( equalTo( b )));
	}


	// EARCAM_SNIPPET_BEGIN: filelist_vanilla
	List<Path> fileListVanilla(Path targetDir)
	{
		try {
			return Files.list(targetDir)
					.collect(toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	// EARCAM_SNIPPET_END: filelist_vanilla



	// EARCAM_SNIPPET_BEGIN: filelist_unexceptional
	List<Path> fileList(Path targetDir)
	{
		return Exceptional.apply(Files::list, targetDir)
				.collect(toList());
	}
	// EARCAM_SNIPPET_END: filelist_unexceptional
	
	
	@Test
	public void applyReadAllBytes()
	{
		Path file = Paths.get("src", "test", "resources", "to_be_read.txt");
		
		String a = new String(readAllBytes(file), UTF_8);
		String b = new String(readAllBytesVanilla(file), UTF_8);
		
		assertThat(a, is(equalTo(b)));
	}
	
	
	// EARCAM_SNIPPET_BEGIN: bytes_vanilla
	public byte[] readAllBytesVanilla(Path file)
	{
		try {
			return Files.readAllBytes(file);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	// EARCAM_SNIPPET_END: bytes_vanilla


	// EARCAM_SNIPPET_BEGIN: bytes_unexceptional
	public byte[] readAllBytes(Path file)
	{
		return Exceptional.apply(Files::readAllBytes, file);
	}
	// EARCAM_SNIPPET_END: bytes_unexceptional
}
