package io.earcam.unexceptional;

/**
 * A checked parallel of {@link java.util.function.ToDoubleFunction}
 * @param <T> the argument type
 * 
 * @see java.util.function.ToDoubleFunction
 */
@FunctionalInterface
public interface CheckedToDoubleFunction<T> {

	/**
	 * See {@link java.util.function.ToDoubleFunction#applyAsDouble(Object)}
	 * 
	 * @param value argument
	 * @return double result of applying argument
	 * @throws Throwable any throwable
	 */
	@SuppressWarnings("squid:S00112")
    public abstract double applyAsDouble(T value) throws Throwable;
}
