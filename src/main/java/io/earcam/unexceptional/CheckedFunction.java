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
 * A checked parallel of {@link java.util.function.Function}
 * 
 * @param <T> argument type
 * @param <R> return type
 * @param <E> the type of Throwable declared
 * 
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface CheckedFunction<T, R, E extends Throwable> {

	/**
	 * See {@link java.util.function.Function#apply(Object)}
	 * 
	 * @param t the function argument
	 * @return the function result
	 * @throws Throwable any throwable
	 * 
	 * @since 0.2.0
	 */
	public abstract R apply(T t) throws E;


	/**
	 * See {@link java.util.function.Function#compose(java.util.function.Function)}
	 * 
	 * @param <V> the type of input to the {@code before} function, and to the
	 * composed function
	 * @param before the function to apply before this function is applied
	 * @return a composed function that first applies the {@code before}
	 * function and then applies this function
	 * @throws NullPointerException if {@code before} is {@code null}
	 * 
	 * @since 0.2.0
	 *
	 * @see java.util.function.Function#compose(java.util.function.Function)
	 */
	public default <V> CheckedFunction<V, R, E> compose(CheckedFunction<? super V, ? extends T, ? extends E> before)
	{
		Objects.requireNonNull(before);
		return (V v) -> apply(before.apply(v));
	}


	/**
	 * See {@link java.util.function.Function#andThen(java.util.function.Function)}
	 * 
	 * @param <V> the type of output of the {@code after} function, and of the
	 * composed function
	 * @param after the function to apply after this function is applied
	 * @return a composed function that first applies this function and then
	 * applies the {@code after} function
	 * @throws NullPointerException if {@code after} is {@code null}
	 * 
	 * @since 0.2.0
	 */
	public default <V> CheckedFunction<T, V, E> andThen(CheckedFunction<? super R, ? extends V, ? extends E> after)
	{
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}


	/**
	 * See {@link java.util.function.Function#identity()}
	 *
	 * @param <T> argument and return type
	 * @return the argument as given
	 * 
	 * @since 0.3.0
	 */
	public static <T, E extends Throwable> CheckedFunction<T, T, E> identity()
	{
		return t -> t;
	}
}
