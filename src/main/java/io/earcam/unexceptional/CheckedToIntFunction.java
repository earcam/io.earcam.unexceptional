/*-
 * #%L
 * io.earcam.unexceptional
 * %%
 * Copyright (C) 2016 - 2017 earcam
 * %%
 * SPDX-License-Identifier: (BSD-3-Clause OR EPL-1.0 OR Apache-2.0 OR MIT)
 * 
 * You <b>must</b> choose to accept, in full - any individual or combination of 
 * the following licenses:
 * <ul>
 * 	<li><a href="https://opensource.org/licenses/BSD-3-Clause">BSD-3-Clause</a></li>
 * 	<li><a href="https://www.eclipse.org/legal/epl-v10.html">EPL-1.0</a></li>
 * 	<li><a href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a></li>
 * 	<li><a href="https://opensource.org/licenses/MIT">MIT</a></li>
 * </ul>
 * #L%
 */
package io.earcam.unexceptional;

/**
 * A checked parallel of {@link java.util.function.ToIntFunction}
 * 
 * @param <T> the argument type
 * @param <E> the type of Throwable declared
 * 
 * @since 0.2.0
 * 
 * @see java.util.function.ToIntFunction
 */
@FunctionalInterface
public interface CheckedToIntFunction<T, E extends Throwable> {

	/**
	 * See {@link java.util.function.ToIntFunction#applyAsInt(Object)}
	 * 
	 * @param value argument
	 * @return int result of applying argument
	 * @throws E any throwable
	 */
	public abstract int applyAsInt(T value) throws E;
}
