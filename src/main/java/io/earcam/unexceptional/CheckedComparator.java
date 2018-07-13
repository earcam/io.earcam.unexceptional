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

import static io.earcam.unexceptional.Exceptional.apply;

import java.io.Serializable; //NOSONAR sonar false positive (suspect Sonar Squid is looking at bytecode here? as erasure of all but left-most bound where multiple bounds occur, see casts of return types in maxBy/minBy)
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * A checked equivalent of {@link java.util.Comparator}, also extends {@link CheckedToIntBiFunction}
 * 
 * @param <T> the type to compare
 * 
 * @since 0.2.0
 * 
 * @see java.util.Comparator
 */
@FunctionalInterface
public interface CheckedComparator<T> extends CheckedToIntBiFunction<T, T> {

	public default int applyAsInt(T t, T u) throws Throwable
	{
		return compare(t, u);
	}


	/**
	 * See {@link java.util.Comparator#compare(Object, Object)}
	 * 
	 * @param o1 first object to compare.
	 * @param o2 second object to compare.
	 * @return a negative integer, zero, or a positive integer as the
	 * first argument is less than, equal to, or greater than the
	 * second, respectively.
	 * @throws Throwable any throwable
	 */
	@SuppressWarnings("squid:S00112")
	public abstract int compare(T o1, T o2) throws Throwable;


	/**
	 * See {@link java.util.Comparator#reversed()}
	 * 
	 * @return an unchecked comparator of the reverse order to {@code this}
	 */
	public default Comparator<T> reversed()
	{
		return Collections.reverseOrder(Exceptional.uncheckComparator(this));
	}


	/**
	 * See {@link java.util.Comparator#thenComparing(Comparator)}
	 * 
	 * @param other the next comparator to be chained (IFF {@code this.compare(a, b)} returns zero)
	 * @return the chained {@link CheckedComparator}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedComparator<T> thenComparing(/* @Nonnull */ CheckedComparator<? super T> other)
	{
		Objects.requireNonNull(other);
		return (CheckedComparator<T> & Serializable) (c1, c2) -> {
			int res = compare(c1, c2);// NOSONAR stfu false positive
			return (res == 0) ? other.compare(c1, c2) : res;
		};
	}


	/**
	 * See {@link java.util.Comparator#thenComparing(java.util.function.Function, Comparator)}
	 * 
	 * @param <U> the type of the sort key
	 * 
	 * @param keyExtractor function used to extract sort key
	 * @param keyComparator comparator used to compare extracted key
	 * @return the chained {@link CheckedComparator}
	 * 
	 * @see #thenComparing(CheckedComparator)
	 */
	public default <U> CheckedComparator<T> thenComparing(CheckedFunction<? super T, ? extends U> keyExtractor, CheckedComparator<? super U> keyComparator)
	{
		return thenComparing(comparing(keyExtractor, keyComparator));
	}


	/**
	 * See {@link java.util.Comparator#thenComparing(java.util.function.Function)}
	 * 
	 * @param <U> the type of the {@link Comparable} sort key
	 * 
	 * @param keyExtractor function used to extract sort key.
	 * @return the chained {@link CheckedComparator}
	 * 
	 * @see #thenComparing(CheckedComparator)
	 */
	public default <U extends Comparable<? super U>> CheckedComparator<T> thenComparing(CheckedFunction<? super T, ? extends U> keyExtractor)
	{
		return thenComparing(comparing(keyExtractor));
	}


	/**
	 * See {@link java.util.Comparator#thenComparingInt(java.util.function.ToIntFunction)}
	 * 
	 * @param keyExtractor function used to extract primitive {@code int} sort key.
	 * @return the chained {@link CheckedComparator}
	 * 
	 * @see #thenComparing(CheckedComparator)
	 */
	public default CheckedComparator<T> thenComparingInt(CheckedToIntFunction<? super T> keyExtractor)
	{
		return thenComparing(comparingInt(keyExtractor));
	}


	/**
	 * See {@link java.util.Comparator#thenComparingLong(java.util.function.ToLongFunction)}
	 * 
	 * @param keyExtractor function used to extract primitive {@code long} sort key.
	 * @return the chained {@link CheckedComparator}
	 * 
	 * @see #thenComparing(CheckedComparator)
	 */
	public default CheckedComparator<T> thenComparingLong(CheckedToLongFunction<? super T> keyExtractor)
	{
		return thenComparing(comparingLong(keyExtractor));
	}


	/**
	 * See {@link java.util.Comparator#thenComparingDouble(java.util.function.ToDoubleFunction)}
	 * 
	 * @param keyExtractor function used to extract primitive {@code double} sort key.
	 * @return the chained {@link CheckedComparator}
	 * @see #thenComparing(CheckedComparator)
	 */
	public default CheckedComparator<T> thenComparingDouble(CheckedToDoubleFunction<? super T> keyExtractor)
	{
		return thenComparing(comparingDouble(keyExtractor));
	}


	/**
	 * See {@link java.util.Comparator#comparing(java.util.function.Function, Comparator)}
	 * 
	 * @param <T> the type of element to be compared
	 * @param <U> the type of the sort key
	 * 
	 * @param keyExtractor function used to extract sort key.
	 * @param keyComparator comparator used to compare extracted key.
	 * @return a comparator that compares by an extracted key using the specified {@code CheckedComparator}.
	 * @throws NullPointerException if either argument is {@code null}.
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public static <T, U> CheckedComparator<T> comparing(/* @Nonnull */ CheckedFunction<? super T, ? extends U> keyExtractor,
			/* @Nonnull */ CheckedComparator<? super U> keyComparator)
	{
		Objects.requireNonNull(keyExtractor);
		Objects.requireNonNull(keyComparator);
		return (CheckedComparator<T> & Serializable) (c1, c2) -> keyComparator.compare(apply(keyExtractor, c1), apply(keyExtractor, c2));
	}


	/**
	 * See {@link java.util.Comparator#comparing(java.util.function.Function)}
	 * 
	 * @param <T> the type of element to be compared.
	 * @param <U> the type of the sort key.
	 * @param keyExtractor the function used to extract the sort key.
	 * @return a comparator that compares by an extracted key.
	 * @throws NullPointerException if the argument is {@code null}.
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public static <T, U extends Comparable<? super U>> CheckedComparator<T> comparing(/* @Nonnull */ CheckedFunction<? super T, ? extends U> keyExtractor)
	{
		Objects.requireNonNull(keyExtractor);
		return (CheckedComparator<T> & Serializable) (c1, c2) -> Exceptional.get(() -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2)));
	}


	/**
	 * See {@link java.util.Comparator#comparingInt(java.util.function.ToIntFunction)}
	 * 
	 * @param <T> the type of element to be compared.
	 * 
	 * @param keyExtractor the function used to extract primitive {@code int} sort key.
	 * @return a checked comparator that compares by an extracted key.
	 * @throws NullPointerException if the argument is {@code null}.
	 * 
	 * @see #comparing(CheckedFunction)
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public static <T> CheckedComparator<T> comparingInt(/* @Nonnull */ CheckedToIntFunction<? super T> keyExtractor)
	{
		Objects.requireNonNull(keyExtractor);
		return (CheckedComparator<T> & Serializable) (c1, c2) -> Exceptional
				.get(() -> Integer.compare(keyExtractor.applyAsInt(c1), keyExtractor.applyAsInt(c2)));
	}


	/**
	 * See {@link java.util.Comparator#comparingLong(java.util.function.ToLongFunction)}
	 * 
	 * @param <T> the type of element to be compared.
	 * 
	 * @param keyExtractor the function used to extract primitive {@code long} sort key.
	 * @return a checked comparator that compares by an extracted key.
	 * @throws NullPointerException if the argument is {@code null}.
	 * 
	 * @see #comparing(CheckedFunction)
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public static <T> CheckedComparator<T> comparingLong(/* @Nonnull */ CheckedToLongFunction<? super T> keyExtractor)
	{
		Objects.requireNonNull(keyExtractor);
		return (CheckedComparator<T> & Serializable) (c1, c2) -> Exceptional
				.get(() -> Long.compare(keyExtractor.applyAsLong(c1), keyExtractor.applyAsLong(c2)));
	}


	/**
	 * See {@link java.util.Comparator#comparingDouble(java.util.function.ToDoubleFunction)}
	 * 
	 * @param <T> the type of element to be compared.
	 * 
	 * @param keyExtractor the function used to extract primitive {@code double} sort key.
	 * @return a checked comparator that compares by an extracted key.
	 * @throws NullPointerException if the argument is {@code null}.
	 * 
	 * @see #comparing(CheckedFunction)
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public static <T> CheckedComparator<T> comparingDouble(/* @Nonnull */ CheckedToDoubleFunction<? super T> keyExtractor)
	{
		Objects.requireNonNull(keyExtractor);
		return (CheckedComparator<T> & Serializable) (c1, c2) -> Exceptional
				.get(() -> Double.compare(keyExtractor.applyAsDouble(c1), keyExtractor.applyAsDouble(c2)));
	}
}
