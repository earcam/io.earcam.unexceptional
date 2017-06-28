package io.earcam.unexceptional;

/**
 * Unchecked exception holding {@link InterruptedException}.  It resets the interrupt flag
 */
public class UncheckedInterruptException extends UncheckedException {

	private static final long serialVersionUID = 5410904583469754965L;

	/**
	 * This constructor sets the interrupt flag
	 * 
	 * see {@link UncheckedException#UncheckedException(Throwable)}
	 * 
	 * @param cause the underlying cause
	 */
	public UncheckedInterruptException(InterruptedException cause)
	{
		super(cause);
		Thread.currentThread().interrupt();
	}
}
