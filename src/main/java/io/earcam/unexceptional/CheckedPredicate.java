package io.earcam.unexceptional;

import java.io.Serializable;   //NOSONAR SonarQube false positive - putting @SuppressWarnings("squid:UselessImportCheck") on class has no effect, can't put at package level either
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A checked parallel of {@link java.util.function.Predicate}
 * 
 * @param <T> argument type
 * 
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface CheckedPredicate<T> {

	/**
	 * See {@link java.util.function.Predicate#test(Object)}
	 * 
	 * @param t argument
	 * @return {@code true} if predicate matches, else {@code false}
	 * @throws Throwable any throwable
	 * @see java.util.function.Predicate#test(Object)
	 */
	@SuppressWarnings("squid:S00112")
	public abstract boolean test(T t) throws Throwable;


	/**
	 * See {@link java.util.function.Predicate#and(java.util.function.Predicate)}
	 * 
	 * @param other the predicate to be compounded with logical <b>and</b>
	 * @return the composite {@link CheckedPredicate}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedPredicate<T> and(@Nonnull CheckedPredicate<? super T> other)
	{
		Objects.requireNonNull(other);
		return (CheckedPredicate<T> & Serializable) t -> test(t) && other.test(t);
	}


	/**
	 * See {@link java.util.function.Predicate#negate()}
	 * 
	 * @return the negated {@link CheckedPredicate}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedPredicate<T> negate()
	{
		return (CheckedPredicate<T> & Serializable) t -> !test(t);
	}


	/**
	 * See {@link java.util.function.Predicate#or(java.util.function.Predicate)}
	 * 
	 * @param other the predicate to be compounded with logical <b>or</b>
	 * @return the composite {@link CheckedPredicate}
	 * @throws NullPointerException if {@code other} is {@code null}
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positive
	public default CheckedPredicate<T> or(@Nonnull CheckedPredicate<? super T> other)
	{
		Objects.requireNonNull(other);
		return (CheckedPredicate<T> & Serializable) t -> test(t) || other.test(t);
	}
}
