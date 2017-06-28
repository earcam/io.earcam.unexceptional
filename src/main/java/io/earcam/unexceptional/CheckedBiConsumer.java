package io.earcam.unexceptional;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A checked parallel of {@link java.util.function.BiConsumer}
 * 
 * @param <T> first argument type
 * @param <U> second argument type
 * 
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U> {

	/**
	 * See {@link java.util.function.BiConsumer#accept(Object, Object)}
	 * 
	 * @param t first argument
	 * @param u second argument
	 * @throws Throwable a possible checked exception
	 */
	public abstract void accept(T t, U u) throws Throwable; // NOSONAR


	/**
	 * See {@link java.util.function.BiConsumer#andThen(java.util.function.BiConsumer)}
	 * 
	 * @param after the bi-consumer to accept sequentially <i>after</i> {@code this}
	 * @return the composite {@link CheckedBiConsumer}
	 * @throws NullPointerException if {@code after} is {@code null}
	 */
	public default CheckedBiConsumer<T, U> andThen(@Nonnull CheckedBiConsumer<? super T, ? super U> after)
	{
		Objects.requireNonNull(after);

		return (CheckedBiConsumer<T, U> & java.io.Serializable) (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}