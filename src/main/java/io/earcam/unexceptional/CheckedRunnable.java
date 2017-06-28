package io.earcam.unexceptional;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A checked parallel of {@link java.lang.Runnable}
 * Essentially {@link java.lang.Runnable} that has the declared propensity to <code>throw</code>
 * 
 * For the equivalent of {@link java.util.concurrent.Callable}, see {@link io.earcam.unexceptional.CheckedSupplier}
 * 
 * @see java.lang.Runnable
 */
@FunctionalInterface
public interface CheckedRunnable {
	
	/**
	 * See {@link Runnable#run()}
	 * 
	 * @throws Exception a checked exception
	 */
	public abstract void run() throws Throwable;  //NOSONAR


	/**
	 * @param after the checked runnable to run sequentially <i>after</i> {@code this}
	 * @return a composite checked runnable of this and then after
	 * @throws NullPointerException if {@code after} is {@code null}
	 */
	public default CheckedRunnable andThen(@Nonnull CheckedRunnable after)
	{
		Objects.requireNonNull(after);
		return (CheckedRunnable & java.io.Serializable)  () -> {
			run();
			after.run();
		};
	}
}
