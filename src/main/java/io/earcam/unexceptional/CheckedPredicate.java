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

import java.io.Serializable;   // NOSONAR SonarQube false positive - putting @SuppressWarnings("squid:UselessImportCheck") on class has no effect, can't put at package level either
import java.util.Objects;

/**
 * A checked parallel of {@link java.util.function.Predicate}
 * 
 * @param <T> argument type
 * @param <E> the type of Throwable declared
 * 
 * @since 0.2.0
 * 
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface CheckedPredicate<T, E extends Throwable> {

	/**
	 * See {@link java.util.function.Predicate#test(Object)}
	 * 
	 * @param t argument
	 * @return {@code true} if predicate matches, else {@code false}
	 * @throws E any throwable
	 * 
	 * @see java.util.function.Predicate#test(Object)
	 */
	public abstract boolean test(T t) throws E;


	/**
	 * See {@link java.util.function.Predicate#and(java.util.function.Predicate)}
	 * 
	 * @param other the predicate to be compounded with logical <b>and</b>
	 * @return the composite {@link CheckedPredicate}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedPredicate<T, E> and(/* @Nonnull */ CheckedPredicate<? super T, ? extends E> other)
	{
		Objects.requireNonNull(other);
		return (CheckedPredicate<T, E> & Serializable) t -> test(t) && other.test(t);
	}


	/**
	 * See {@link java.util.function.Predicate#negate()}
	 * 
	 * @return the negated {@link CheckedPredicate}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedPredicate<T, E> negate()
	{
		return (CheckedPredicate<T, E> & Serializable) t -> !test(t);
	}


	/**
	 * See {@link java.util.function.Predicate#or(java.util.function.Predicate)}
	 * 
	 * @param other the predicate to be compounded with logical <b>or</b>
	 * @return the composite {@link CheckedPredicate}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedPredicate<T, E> or(/* @Nonnull */ CheckedPredicate<? super T, ? extends E> other)
	{
		Objects.requireNonNull(other);
		return (CheckedPredicate<T, E> & Serializable) t -> test(t) || other.test(t);
	}
}
