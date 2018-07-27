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

import java.util.Objects;

/**
 * A checked exception version of {@link java.util.function.Consumer}
 * 
 * @param <T> the input type of the {@link #accept(Object)} operation
 * @param <E> the type of Throwable declared
 * 
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable> {

	/**
	 * See {@link java.util.function.Consumer#accept(Object)}
	 * 
	 * @param t the input argument
	 * @throws E a possible checked exception
	 */
	public abstract void accept(T t) throws E;


	/**
	 * See {@link java.util.function.Consumer#andThen(Consumer)}
	 * 
	 * @param after the operation to perform after this operation
	 * @return a composed {@code CheckedConsumer} that performs in sequence this
	 * operation followed by the {@code after} operation
	 * 
	 * @throws NullPointerException if {@code after} is {@code null}
	 */
	public default CheckedConsumer<T, E> andThen(/* @Nonnull */ CheckedConsumer<? super T, ? extends E> after)
	{
		Objects.requireNonNull(after);
		return (T t) -> {
			accept(t);
			after.accept(t);
		};
	}
}
