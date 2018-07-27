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
 * A checked parallel of {@link java.util.function.BiConsumer}
 * 
 * @param <T> first argument type
 * @param <U> second argument type
 * @param <E> the type of Throwable declared
 * 
 * @since 0.2.0
 * 
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U, E extends Throwable> {

	/**
	 * See {@link java.util.function.BiConsumer#accept(Object, Object)}
	 * 
	 * @param t first argument
	 * @param u second argument
	 * @throws E a possible checked exception
	 */
	public abstract void accept(T t, U u) throws E;


	/**
	 * See {@link java.util.function.BiConsumer#andThen(java.util.function.BiConsumer)}
	 * 
	 * @param after the bi-consumer to accept sequentially <i>after</i> {@code this}
	 * @return the composite {@link CheckedBiConsumer}
	 * @throws NullPointerException if {@code after} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public default CheckedBiConsumer<T, U, E> andThen(CheckedBiConsumer<? super T, ? super U, ? extends E> after)
	{
		Objects.requireNonNull(after);

		return (CheckedBiConsumer<T, U, E> & java.io.Serializable) (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}
