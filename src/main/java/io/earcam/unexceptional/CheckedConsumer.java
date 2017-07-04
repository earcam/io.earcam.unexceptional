package io.earcam.unexceptional;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A checked exception version of {@link java.util.function.Consumer}
 * 
 * @param <T> the input type of the {@link #accept(Object)} operation
 * 
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

	/**
     * See {@link java.util.function.Consumer#accept(Object)}
     * 
     * @param t the input argument
	 * @throws Exception a possible checked exception
	 */
	@SuppressWarnings("squid:S00112")
	public abstract void accept(T t) throws Throwable;

    /**
     * See {@link java.util.function.Consumer#andThen(Consumer)}
     * 
     * @param after the operation to perform after this operation
     * @return a composed {@code CheckedConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * 
     * @throws NullPointerException if {@code after} is {@code null}
     */
    public default CheckedConsumer<T> andThen(@Nonnull CheckedConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { 
			accept(t); 
			after.accept(t); 
		};
    }
}
