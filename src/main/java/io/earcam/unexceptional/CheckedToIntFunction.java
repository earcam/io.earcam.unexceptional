package io.earcam.unexceptional;


/**
 * A checked parallel of {@link java.util.function.ToIntFunction}
 * @param <T> the argument type
 * 
 * @see java.util.function.ToIntFunction
 */
@FunctionalInterface
public interface CheckedToIntFunction<T> {

	/**
	 * See {@link java.util.function.ToIntFunction#applyAsInt(Object)}
	 * @param value argument
	 * @return int result of applying argument
	 * @throws Throwable any throwable
	 */
	@SuppressWarnings("squid:S00112")
    public abstract int applyAsInt(T value) throws Throwable;
}
