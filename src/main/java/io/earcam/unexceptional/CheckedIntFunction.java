package io.earcam.unexceptional;

/**
 * A checked parallel of {@link java.util.function.IntFunction}
 * @param <R> return type
 * 
 * @see java.util.function.IntFunction
 */
@FunctionalInterface
public interface CheckedIntFunction<R> {

	/**
	 * See {@link java.util.function.IntFunction#apply(int)}
	 * @param value primitive {@code int} argument
	 * @return result
	 * @throws Throwable any throwable
	 */
	@SuppressWarnings("squid:S00112")
	public abstract R apply(int value) throws Throwable;
}
