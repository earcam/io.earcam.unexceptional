package io.earcam.unexceptional;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * A general purpose unchecked exception, should probably be abstract, so use sparingly and only when lazy
 * Prefer the more specific subclasses
 */
@ParametersAreNullableByDefault
public class UncheckedException extends RuntimeException {

	private static final long serialVersionUID = -8070649283076874939L;


	/**
	 * see {@link RuntimeException#RuntimeException(Throwable)}
	 * 
	 * @param cause the underlying cause
	 */
	public UncheckedException(Throwable cause)
	{
		super(cause);
	}


	/**
	 * see {@link RuntimeException#RuntimeException(String)}
	 * 
	 * @param message the detail message
	 */
	public UncheckedException(String message)
	{
		super(message);
	}


	/**
	 * see {@link RuntimeException#RuntimeException(String, Throwable)}
	 * 
	 * @param message the detail message
	 * @param cause the underlying cause
	 */
	public UncheckedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
