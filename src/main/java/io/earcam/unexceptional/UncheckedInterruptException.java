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
package io.earcam.unexceptional;

/**
 * Unchecked exception holding {@link InterruptedException}. It <b>resets</b> the interrupt flag.
 * 
 * @since 0.2.0
 */
public class UncheckedInterruptException extends UncheckedException {

	private static final long serialVersionUID = 5410904583469754965L;


	/**
	 * This constructor also sets the interrupt flag
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
