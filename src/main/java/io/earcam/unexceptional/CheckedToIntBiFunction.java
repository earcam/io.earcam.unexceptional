package io.earcam.unexceptional;

/**
 * A function accepting 2 arguments and supplying an {@code int} return
 *   
 * @param <T> first argument type
 * @param <U> last argument type
 */
@FunctionalInterface
public interface CheckedToIntBiFunction<T, U> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     * @throws Throwable Any throwable
     */
    int applyAsInt(T t, U u) throws Throwable;		//NOSONAR

}
