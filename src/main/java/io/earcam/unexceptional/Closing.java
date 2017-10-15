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

import static io.earcam.unexceptional.Exceptional.uncheck;

import java.io.IOException;

import javax.annotation.WillClose;

/**
 * <p>
 * Succinctly handle {@link AutoCloseable}s that throw checked {@link Exception}s.
 * <p>
 * 
 * <p>
 * There are two families of static methods; {@code closeAfterApplying(...)} and {@code closeAfterAccepting(...)}, the
 * former <i>applies</i> checked functions (the use-case being <b>read</b>) while the later <i>accepts</i> checked 
 * consumers (the use-case being <b>write</b>)
 * </p>
 * 
 * <p>
 * </p>
 * 
 * <p>
 * <b>Example</b>: this zero-branch, single-instruction, one-liner will find a free port number:
 * </p>
 * 
 * <p>
 * &nbsp;&nbsp;&nbsp;<code>   int port = Closing.closeAfterApplying(ServerSocket::new, 0, ServerSocket::getLocalPort);</code>
 * </p>
 * 
 * <p>
 * <b>Motivation</b>: I/O stream instances typically (and necessarily) requires handling a lot of possible
 * {@link IOException}s - by invoking:
 * <ul>
 * <li>Constructor</li>
 * <li>A read/write call</li>
 * <li>A call to {@link AutoCloseable#close()}</li>
 * <li>A read/write call, then subsequently in the call to {@link AutoCloseable#close()} - via
 * {@code try}-with-resources</li>
 * </ul>
 * </p>
 * <p>
 * That's a lot of branches to cover - often it is irrelevant to the application which branch actually throws, but
 * regardless test coverage suffers without some needless stub/mocked throw-on-create/read/write/close tests.
 * </p>
 * 
 * <p>
 * </p>
 */
public final class Closing {

	private Closing()
	{
		throw new IllegalStateException("Why on earth would you want to instantiate this?");
	}


	/**
	 * <p>
	 * Ultra-shorthand for {@link AutoCloseable}/{@link java.io.Closeable}, obvious use for {@link java.io.InputStream}
	 * </p>
	 * @param create a function applying {@code t} to produce an {@link AutoCloseable} of type {@code <C>}
	 * @param t the argument to apply to the {@code create} function
	 * @param convert a function applied to the {@link AutoCloseable} to produce the result
	 * 
	 * @param <C> {@link AutoCloseable} type
	 * @param <T> {@code create} function argument type
	 * @param <R> the result type
	 * 
	 * @return the result of applying the {@code convert} function
	 */
	public static <C extends AutoCloseable, T, R> R closeAfterApplying(CheckedFunction<T, C> create, T t, CheckedFunction<C, R> convert)
	{
		C closeable = Exceptional.apply(create, t);
		return closeAfterApplying(closeable, convert);
	}


	/**
	 * Applies the function to the closeable, returning the result and closing the closable - checked exceptions are
	 * rethrown as unchecked.
	 * @param closeable the closeable subject of the {@code convert} function
	 * @param convert the function consuming the closeable and supplying the result
	 * 
	 * @param <C> the auto-closeable type, to apply and close
	 * @param <R> the result type
	 * 
	 * @return the result of applying {@code convert} function to the {@code closeable} argument
	 */
	public static <C extends AutoCloseable, R> R closeAfterApplying(@WillClose C closeable, CheckedFunction<C, R> convert)
	{
		try(C autoClose = closeable) {
			return Exceptional.apply(convert, autoClose);
		} catch(Exception e) {
			throw uncheck(e);
		}
	}


	/**
	 * Applies the bi-function to the closeable, returning the result and closing the closable - checked exceptions are
	 * rethrown as unchecked.
	 * @param closeable the closeable subject of the {@code convert} function
	 * @param instance
	 * @param convert the function consuming the closeable and supplying the result
	 * 
	 * @param <C> the auto-closeable type, will be applied and closed
	 * @param <U> the type of second argument to bi-function apply
	 * @param <R> the result type
	 * 
	 * @return the result of applying {@code convert} function to the {@code closeable} argument
	 */
	public static <C extends AutoCloseable, U, R> R closeAfterApplying(@WillClose C closeable, U instance, CheckedBiFunction<C, U, R> convert)
	{
		try(C autoClose = closeable) {
			return Exceptional.apply(convert, autoClose, instance);
		} catch(Exception e) {
			throw uncheck(e);
		}
	}


	/**
	 * Applies the {@code create} function to {@code t}, resulting in a {@link AutoCloseable} which is closed after
	 * being consumed.
	 * Checked exceptions are rethrown as unchecked.
	 * @param create the function creating the {@link AutoCloseable}
	 * @param t the argument that the {@code create} function is applied to
	 * @param consume the consumer of the {@link AutoCloseable}
	 * 
	 * @param <C> the auto-closeable type, to be created, consumed and closed
	 * @param <T> the function's argument type, used to create the auto-closeable
	 */
	public static <C extends AutoCloseable, T> void closeAfterAccepting(CheckedFunction<T, C> create, T t, CheckedConsumer<C> consume)
	{
		C closeable = Exceptional.apply(create, t);
		closeAfterAccepting(closeable, consume);
	}


	/**
	 * Consumes the {@code closeable} before closing. Checked exceptions are rethrown as unchecked.
	 * @param closeable the closeable to be consumed and closed
	 * @param consume the consumer of the {@link AutoCloseable}
	 * 
	 * @param <C> the auto-closeable type
	 */
	public static <C extends AutoCloseable> void closeAfterAccepting(@WillClose C closeable, CheckedConsumer<C> consume)
	{
		try(C autoClose = closeable) {
			Exceptional.accept(consume, autoClose);
		} catch(Exception e) {
			throw uncheck(e);
		}
	}


	public static <C extends AutoCloseable, T, U> void closeAfterAccepting(CheckedFunction<T, C> create, T t, U instance, CheckedBiConsumer<C, U> consume)
	{
		C closeable = Exceptional.apply(create, t);
		closeAfterAccepting(closeable, instance, consume);
	}


	/**
	 * Consumes both the {@code closeable} and {@code instance} before closing. Checked exceptions are rethrown as
	 * unchecked.
	 * @param closeable the closeable to be consumed and closed
	 * @param instance the instance to consume
	 * @param consume the consumer of the {@link AutoCloseable}
	 * 
	 * @param <C> the auto-closeable type to consume
	 * @param <U> the type of consumer's second argument
	 */
	public static <C extends AutoCloseable, U> void closeAfterAccepting(@WillClose C closeable, U instance, CheckedBiConsumer<C, U> consume)
	{
		try(C autoClose = closeable) {
			Exceptional.accept(consume, autoClose, instance);
		} catch(Exception e) {
			throw uncheck(e);
		}
	}
}
