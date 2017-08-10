package io.earcam.unexceptional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import io.earcam.unexceptional.UncheckedException;

public class UncheckedExceptionTest {

	@Test
	public void message()
	{
		String message = "message";
		UncheckedException e = new UncheckedException(message);

		assertThat(e.getMessage(), is(message));
		assertThat(e.getCause(), is(nullValue()));
	}


	@Test
	public void cause()
	{
		Throwable cause = new Throwable();
		UncheckedException e = new UncheckedException(cause);

		assertThat(e.getCause(), is(cause));
		assertThat(e.getMessage(), is(Throwable.class.getCanonicalName()));
	}


	@Test
	public void messageAndCause()
	{
		String message = "message";
		Throwable cause = new Throwable();
		UncheckedException e = new UncheckedException(message, cause);

		assertThat(e.getCause(), is(cause));
		assertThat(e.getMessage(), is(message));
	}
}
