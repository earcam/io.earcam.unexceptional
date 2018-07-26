/*-
 * #%L
 * io.earcam.unexceptional
 * %%
 * Copyright (C) 2016 - 2018 earcam
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

import java.util.Objects;
import java.util.function.IntConsumer;

/**
 * 
 * @param <E> the type of Throwable declared
 * 
 * @since 0.5.0
 * 
 * @see java.util.function.IntConsumer
 */
@FunctionalInterface
public interface CheckedIntConsumer<E extends Throwable> {

	public abstract void accept(int value) throws E;


	/**
	 * @param after the operation to perform after this operation
	 * @return a composed {@code CheckedIntConsumer} that performs in sequence this
	 * operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 * 
	 * @see java.util.function.IntConsumer#andThen(IntConsumer)
	 */
	default CheckedIntConsumer<E> andThen(CheckedIntConsumer<? extends E> after)
	{
		Objects.requireNonNull(after);
		return (int t) -> {
			accept(t);
			after.accept(t);
		};
	}

}
