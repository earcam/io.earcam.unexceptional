package io.earcam.unexceptional;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * /** A checked parallel of {@link java.util.function.BiFunction}
 * 
 * @param <T> first argument type
 * @param <U> second argument type
 * @param <R> return type
 * 
 * @see java.util.function.BiFunction
 */
@FunctionalInterface
public interface CheckedBiFunction<T, U, R> {

	/**
	 * See {@link java.util.function.BiFunction#apply(Object, Object)}
	 * 
	 * @param t first argument.
	 * @param u second argument.
	 * @return result of applying {@code this} function.
	 * @throws Throwable a possible checked exception
	 */
	@SuppressWarnings("squid:S00112")
	public abstract R apply(T t, U u) throws Throwable;


	/**
	 * See {@link java.util.function.BiFunction#andThen(java.util.function.Function)}
	 * 
	 * @param <V> the return type of the {@code after} parameter
	 * 
	 * @param after the {@link CheckedFunction} to be chained (taking result of {@code this} as argument) 
	 * @return the composite {@link CheckedBiFunction}
	 * @throws NullPointerException if {@code after} is {@code null}
	 */
	public default <V> CheckedBiFunction<T, U, V> andThen(@Nonnull CheckedFunction<? super R, ? extends V> after)
	{
		Objects.requireNonNull(after);
		return (CheckedBiFunction<T, U, V> & java.io.Serializable) (T t, U u) -> after.apply(apply(t, u));
	}
}
