package io.earcam.unexceptional;
import java.security.GeneralSecurityException;

/**
 * Unchecked exception holding {@link GeneralSecurityException}
 */
public class UncheckedSecurityException extends UncheckedException {

	private static final long serialVersionUID = 7088535108183355628L;

	/**
	 * see {@link UncheckedException#UncheckedException(Throwable)}
	 * 
	 * @param cause the underlying cause
	 */
	public UncheckedSecurityException(GeneralSecurityException cause)
	{
		super(cause);
	}
}
