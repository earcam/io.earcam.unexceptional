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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;//NOSONAR stfu false positive (suspect Sonar Squid is looking at bytecode here? as erasure of all but left-most bound where multiple bounds occur, see casts of return types in maxBy/minBy)
import java.util.Comparator;
import java.util.Objects;

/**
 * A checked parallel of {@link java.util.function.BinaryOperator}
 * 
 * @param <T> arguments and return type
 * 
 * @since 0.2.0
 * 
 * @see java.util.function.BinaryOperator
 */
@FunctionalInterface
public interface CheckedBinaryOperator<T> extends CheckedBiFunction<T, T, T> {

	/**
	 * See {@link java.util.function.BinaryOperator#minBy(Comparator)}
	 *
	 * @param <T> arguments and return type
	 * 
	 * @param comparator the comparator used to determine the minimum.
	 * @return a {@link CheckedBinaryOperator}
	 * @throws NullPointerException if {@code comparator} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public static <T> CheckedBinaryOperator<T> minBy(/* @Nonnull */ CheckedComparator<? super T> comparator)
	{
		requireNonNull(comparator);
		return (CheckedBinaryOperator<T> & Serializable) (a, b) -> comparator.compare(a, b) < 0 ? a : b;
	}


	/**
	 * See {@link java.util.function.BinaryOperator#maxBy(Comparator)}
	 * 
	 * @param <T> arguments and return type
	 * 
	 * @param comparator the comparator used to determine the maximum.
	 * @return a {@link CheckedBinaryOperator}
	 * @throws NullPointerException if {@code comparator} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public static <T> CheckedBinaryOperator<T> maxBy(/* @Nonnull */ CheckedComparator<? super T> comparator)
	{
		Objects.requireNonNull(comparator);
		return (CheckedBinaryOperator<T> & Serializable) (a, b) -> comparator.compare(a, b) > 0 ? a : b;
	}
}
