package io.earcam.unexceptional;

/**
 * Unchecked exception holding {@link ReflectiveOperationException}
 */
public class UncheckedReflectiveException extends UncheckedException {

	private static final long serialVersionUID = 5579177178944916001L;

	/**
	 * see {@link UncheckedException#UncheckedException(Throwable)}
	 * 
	 * @param cause the underlying cause
	 */
	public UncheckedReflectiveException(ReflectiveOperationException cause)
	{
		super(cause);
	}
}
