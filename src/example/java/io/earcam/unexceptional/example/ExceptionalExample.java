package io.earcam.unexceptional.example;

import static io.earcam.unexceptional.Exceptional.apply;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.ToIntFunction;

import org.junit.Test;

import io.earcam.unexceptional.Exceptional;
import io.earcam.unexceptional.UncheckedException;


/**
 * In the tests below, {@code a} is equivalent to {@code b} - the notable difference being the lack of try-catch wrapping
 */
public class ExceptionalExample {

	
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
		
		URI a;
		try {
			a = new URI(ʊri);
		} catch (URISyntaxException e) {
			throw new UncheckedException(e);
		}

		
		URI b = Exceptional.uri(ʊri);

		
		assertThat(a, is( equalTo( b )));
	}
	
	
	
	@Test
	public void applyFilesList()
	{
		Path targetDir = Paths.get(".", "target");


		List<Path> a = null;
		try {
			a = Files.list(targetDir)
					.collect(toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		
		
		List<Path> b = apply(Files::list, targetDir)
				.collect(toList());


		assertThat(a, is( equalTo( b )));
	}


	@Test
	public void findFreePort()
	{
		int portA = 0; 
		try(ServerSocket socket = new ServerSocket(0)) {
			portA = socket.getLocalPort();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		int portB = Exceptional.closeAfterApplying(ServerSocket::new, 0, ServerSocket::getLocalPort);
		
		assertThat(portA, is( not( equalTo( 0 ))));
		assertThat(portB, is( not( equalTo( 0 ))));
	}
}
