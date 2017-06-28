package io.earcam.unexceptional;

/**
 * A checked parallel of {@link java.util.function.ToLongFunction}
 * @param <T> the argument type
 * 
 * @see java.util.function.ToLongFunction
 */
@FunctionalInterface
public interface CheckedToLongFunction<T> {

	/**
	 * See {@link java.util.function.ToLongFunction#applyAsLong(Object)}
	 * @param value argument
	 * @return long result of applying argument
	 * @throws Throwable any throwable
	 */
    public abstract long applyAsLong(T value) throws Throwable;  //NOSONAR
}
