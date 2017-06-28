package io.earcam.unexceptional;

/**
 * A checked parallel of {@link java.util.function.Supplier}
 * @param <T> the type of supplied Object
 * 
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

	/**
	 * See {@link java.util.function.Supplier#get()}
	 * @return result
	 * @throws Throwable any throwable
	 */
	public abstract T get() throws Throwable;  //NOSONAR
}
