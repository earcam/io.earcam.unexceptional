package io.earcam.unexceptional;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;//NOSONAR stfu false positive (suspect Sonar Squid is looking at bytecode here? as erasure of all but left-most bound where multiple bounds occur, see casts of return types in maxBy/minBy)
import java.util.Comparator;
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A checked parallel of {@link java.util.function.BinaryOperator}
 * 
 * @param <T> arguments and return type
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
	public static <T> CheckedBinaryOperator<T> minBy(@Nonnull CheckedComparator<? super T> comparator)
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
	public static <T> CheckedBinaryOperator<T> maxBy(@Nonnull CheckedComparator<? super T> comparator)
	{
		Objects.requireNonNull(comparator);
		return (CheckedBinaryOperator<T> & Serializable) (a, b) -> comparator.compare(a, b) > 0 ? a : b;
	}
}
