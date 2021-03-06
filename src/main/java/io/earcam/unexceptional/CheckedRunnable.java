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

import java.io.Serializable;   //NOSONAR SonarQube false positive - putting @SuppressWarnings("squid:UselessImportCheck") on class has no effect, can't put at package level either
import java.util.Objects;

/**
 * A checked parallel of {@link java.lang.Runnable}
 * Essentially {@link java.lang.Runnable} that has the declared propensity to <code>throw</code>
 * 
 * For the equivalent of {@link java.util.concurrent.Callable}, see {@link io.earcam.unexceptional.CheckedSupplier}
 * 
 * @param <E> the type of Throwable declared
 * 
 * @since 0.2.0
 * 
 * @see java.lang.Runnable
 */
@FunctionalInterface
public interface CheckedRunnable<E extends Throwable> {

	/**
	 * See {@link Runnable#run()}
	 * 
	 * @throws E a checked exception
	 */
	public abstract void run() throws E;


	/**
	 * @param after the checked runnable to run sequentially <i>after</i> {@code this}
	 * @return a composite checked runnable of this and then after
	 * @throws NullPointerException if {@code after} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedRunnable<E> andThen(/* @Nonnull */ CheckedRunnable<? extends E> after)
	{
		Objects.requireNonNull(after);
		return (CheckedRunnable<E> & Serializable) () -> {
			run();
			after.run();
		};
	}
}
