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

import static java.util.Collections.unmodifiableMap;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.security.GeneralSecurityException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * <p>
 * Static utility for easy conversion of checked exceptions, and invocation of methods declaring checked exceptions.
 * </p>
 * 
 * <p>
 * There are many cases where you <i>know</i>, within a given context that a checked exception will not be raised, but
 * if it is, then it's certainly <i>game over</i>. An example being URLs in server configuration.
 * </p>
 * 
 * <p>
 * The functionality centres around {@link java.util.function} given their beautifully broad application.
 * </p>
 * 
 * <p>
 * In addition, common standard library <i>annoyances</i> are included; e.g. {@link URL}, {@link URI}, and highly
 * concise (re-read as terse one-liner - arguably laconic over expressive) for common IO streams usage.
 * </p>
 * 
 * <p>
 * When dealing with {@link Stream} from an IO source (e.g. the file system), it may be preferable (to pay a
 * performance cost due to wrapping) and use {@link EmeticStream} which mirrors {@link Stream} functionality
 * but with checked equivalent types.
 * </p>
 * 
 * <h1>Exceptional</h1>
 * <b>/ɪkˈsɛpʃ(ə)n(ə)l,ɛkˈsɛpʃ(ə)n(ə)l/</b>
 * <p>
 * <i>adjective:</i>
 * </p>
 * <p>
 * 1. unusual; not typical.
 * </p>
 */
@ParametersAreNonnullByDefault
@Immutable
public final class Exceptional implements Serializable {

	private static final long serialVersionUID = -1350140594550206145L;

	private static final Map<Class<? extends Throwable>, Function<Throwable, RuntimeException>> UNCHECK_MAP;

	static {
		Map<Class<? extends Throwable>, Function<Throwable, RuntimeException>> map = new HashMap<>();
		// @formatter:off
		map.put(ReflectiveOperationException.class, e -> new UncheckedReflectiveException((ReflectiveOperationException)e));
		map.put(    GeneralSecurityException.class, e -> new UncheckedSecurityException((GeneralSecurityException)e));
		map.put(        InterruptedException.class, e -> new UncheckedInterruptException((InterruptedException)e));
		map.put(          URISyntaxException.class, e -> new UncheckedUriSyntaxException((URISyntaxException)e));
		map.put(            RuntimeException.class, RuntimeException.class::cast);
		map.put(                 IOException.class, e -> new UncheckedIOException((IOException)e));
		// @formatter:on

		UNCHECK_MAP = unmodifiableMap(map);
	}

	/**
	 * An {@link UncaughtExceptionHandler} that simply rethrows,
	 * wrapping in an appropriate unchecked if necessary
	 * 
	 * @since 0.2.0
	 */
	public static final UncaughtExceptionHandler RETHROWING = (t, e) -> Exceptional.rethrow(e);

	/**
	 * An {@link UncaughtExceptionHandler} that simply swallows <i>all</i> exceptions,
	 * except subclasses of {@link Error}, which are rethrown.
	 * 
	 * @since 0.2.0
	 */
	public static final UncaughtExceptionHandler SWALLOWING = (t, e) -> Exceptional.swallow(e);


	private Exceptional()
	{
		throw new IllegalStateException("Why on earth would you want to instantiate this?");
	}


	/**
	 * @param earl the text URL
	 * @return a {@link URL} representation
	 * 
	 * @since 0.2.0
	 */
	public static URL url(CharSequence earl)
	{
		try {
			return new URL(earl.toString());
		} catch(MalformedURLException e) {
			throw uncheck(e);
		}
	}


	/**
	 * @param protocol e.g. https, ftp, sctp
	 * @param host hostname or IP
	 * @param port 0 to 65536
	 * @param path the "file" portion of the URL
	 * @param handler optional URLStreamHandler
	 * @return a {@link URL} representation
	 * 
	 * @since 0.2.0
	 */
	public static URL url(String protocol, String host, int port, String path, @Nullable URLStreamHandler handler)
	{
		try {
			return new URL(protocol, host, port, path, handler);
		} catch(MalformedURLException e) {
			throw uncheck(e);
		}
	}


	/**
	 * @param ʊri the text URI (as Earl is to URL, so ʊri (as in Uri Geller) is to URI)
	 * @return a {@link URI} representation
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S00117")  // utf8 in a parameter name is fine by me
	public static URI uri(CharSequence ʊri)
	{
		try {
			return new URI(ʊri.toString());
		} catch(URISyntaxException e) {
			throw uncheck(e);
		}
	}


	/**
	 * Invokes {@link URL#toURI()}, catching any {@link URISyntaxException} and rethrowing
	 * wrapped as {@link UncheckedUriSyntaxException}
	 * 
	 * @param earl the {@link URL} to convert
	 * @return the {@link URI} form of the <code>earl</code> argument
	 * 
	 * @since 0.3.0
	 */
	public static URI uri(URL earl)
	{
		try {
			return earl.toURI();
		} catch(URISyntaxException e) {
			throw uncheck(e);
		}
	}


	/**
	 * Will regurgitate any {@link Error} - otherwise will dutifully and silently <i>swallow</i> whole.
	 * Gulping of {@link InterruptedException}s will result in the current {@link Thread}'s interrupt
	 * flag being reset.
	 * 
	 * <b>Caution</b>: used carelessly/incorrectly this foul method will inevitably lead to a frustrating
	 * debugging session resulting in plenty of "doh"/"wtf" but never a "eureka" moment. Shooting of
	 * messenger is in breach of license terms.
	 * 
	 * @param caught the caught unmentionable
	 * 
	 * @since 0.2.0
	 */
	public static void swallow(Throwable caught)
	{
		resetIfInterrupt(caught);
		if(caught instanceof Error) {
			rethrow(caught);
		}
	}


	/**
	 * Catching an {@link InterruptedException} clears the interrupt flag,
	 * this merely resets the flag IFF the <code>thrown</code> parameter is
	 * an instance of {@link InterruptedException}.
	 * 
	 * @param thrown possible {@link InterruptedException}
	 * 
	 * @since 0.2.0
	 */
	public static void resetIfInterrupt(Throwable thrown)
	{
		if(thrown instanceof InterruptedException) {
			Thread.currentThread().interrupt();
		}
	}


	/**
	 * Directly rethrows {@link Error}s or {@link RuntimeException}s, wraps
	 * checked exceptions appropriately
	 * 
	 * @param thrown the caught throwable to be rethrown as unchecked
	 * @return actually nothing, this just allows you to write {@code throw Exceptional.rethrow(e)} for methods than
	 * have return values.
	 * 
	 * @see #throwAsUnchecked(Throwable)
	 * 
	 * @since 0.2.0
	 */
	public static RuntimeException rethrow(Throwable thrown)
	{
		if(thrown instanceof Error) {
			throw (Error) thrown;
		}
		throw uncheck(thrown);
	}


	/**
	 * Invokes {@link CheckedRunnable#run()} catching any checked
	 * {@link Exception}s rethrowing them as unchecked.
	 * 
	 * @param runnable the checked runnable to run
	 * 
	 * @since 0.2.0
	 */
	public static void run(CheckedRunnable runnable)
	{
		try {
			runnable.run();
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			rethrow(thrown);
		}
	}


	/**
	 * Convert a {@link CheckedRunnable} into a {@link Runnable}
	 * 
	 * @param runnable the checked runnable to wrap
	 * @return an unchecked wrapper around the <code>runnable</code> argument
	 * 
	 * @since 0.2.0
	 */
	public static Runnable uncheckRunnable(CheckedRunnable runnable)
	{
		return () -> run(runnable);
	}


	/**
	 * Invokes {@link Callable#call()} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> the return type of the {@link Callable}
	 * 
	 * @param callable the {@link Callable} to execute
	 * @return the result of calling the {@link Callable}
	 * 
	 * @since 0.2.0
	 */
	public static <T> T call(Callable<T> callable)
	{
		try {
			return callable.call();
		} catch(Exception thrown) {  // NO SONAR
			throw uncheck(thrown);
		}
	}


	/**
	 * <p>
	 * Converts {@link Throwable}s to {@link RuntimeException}s.
	 * </p>
	 * <p>
	 * If the supplied {@link Throwable} is already a {@link RuntimeException}, then it's simply cast and returned.
	 * </p>
	 * <p>
	 * {@link Error} subclasses will be wrapped in an {@link UncheckedException}.
	 * </p>
	 * <p>
	 * The interrupt flag will be reset IFF {@code caught instanceof InterruptedException}
	 * </p>
	 * 
	 * @param caught any {@link Throwable}
	 * @return a {@link RuntimeException}, typically a subclass of {@link UncheckedException} or an
	 * {@link UncheckedIOException}
	 * @see #rethrow(Throwable)
	 * 
	 * @since 0.2.0
	 */
	public static RuntimeException uncheck(Throwable caught)
	{
		return UNCHECK_MAP.entrySet().stream()
				.filter(e -> e.getKey().isInstance(caught))
				.map(e -> e.getValue().apply(caught))
				.findFirst()
				.orElseGet(() -> new UncheckedException(caught));
	}


	/**
	 * Converts {@link CheckedConsumer} to {@link Consumer}
	 * 
	 * @param <T> the consumed type
	 * 
	 * @param consumer a consumer that declares checked exception(s)
	 * @return a vanilla {@link java.util.function.Consumer}
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> Consumer<T> uncheckConsumer(CheckedConsumer<T> consumer)
	{
		return (Consumer<T> & Serializable) t -> Exceptional.accept(consumer, t);
	}


	/**
	 * Invokes {@link CheckedConsumer#accept(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> the consumed type
	 * 
	 * @param consumer the consumer of the {@code value}
	 * @param value the value to be consumed
	 * 
	 * @since 0.2.0
	 */
	public static <T> void accept(CheckedConsumer<T> consumer, T value)
	{
		try {
			consumer.accept(value);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			rethrow(thrown);
		}
	}


	/**
	 * Converts {@link CheckedIntConsumer} to {@link IntConsumer}
	 * 
	 * @param consumer a consumer of primitive integer that declares checked exception(s)
	 * @return a vanilla {@link java.util.function.IntConsumer}
	 * 
	 * @since 0.5.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static IntConsumer uncheckIntConsumer(CheckedIntConsumer consumer)
	{
		return (IntConsumer & Serializable) t -> Exceptional.accept(consumer, t);
	}


	/**
	 * Invokes {@link CheckedIntConsumer#accept(int)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param consumer the consumer of the {@code int value}
	 * @param value the value to be consumed
	 * 
	 * @since 0.5.0
	 */
	public static void accept(CheckedIntConsumer consumer, int value)
	{
		try {
			consumer.accept(value);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			rethrow(thrown);
		}
	}


	/**
	 * Convert a {@link CheckedBiConsumer} into a {@link BiConsumer}.
	 * 
	 * @param <T> first argument type
	 * @param <U> last argument type
	 * 
	 * @param consumer the checked consumer
	 * @return an unchecked consumer wrapping the {@code consumer} argument
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T, U> BiConsumer<T, U> uncheckBiConsumer(CheckedBiConsumer<T, U> consumer)
	{
		return (BiConsumer<T, U> & Serializable) (t, u) -> Exceptional.accept(consumer, t, u);
	}


	/**
	 * Invokes {@link CheckedConsumer#accept(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> first argument type
	 * @param <U> last argument type
	 * 
	 * @param consumer the consumer of the {@code value}
	 * @param t first argument to be consumed
	 * @param u last argument to be consumed
	 * 
	 * @since 0.2.0
	 */
	public static <T, U> void accept(CheckedBiConsumer<T, U> consumer, T t, U u)
	{
		try {
			consumer.accept(t, u);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			rethrow(thrown);
		}
	}


	/**
	 * Converts a {@link CheckedFunction} into a {@link Function}.
	 * 
	 * @param <T> argument type
	 * @param <R> return type
	 * 
	 * @param function the checked function
	 * @return an unchecked function wrapping the {@code function} argument.
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T, R> Function<T, R> uncheckFunction(CheckedFunction<T, R> function)
	{
		return (Function<T, R> & Serializable) t -> Exceptional.apply(function, t);
	}


	/**
	 * Invokes {@link CheckedFunction#apply(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> argument type
	 * @param <R> return type
	 * 
	 * @param function the checked function to invoke with the {@code argument}
	 * @param argument the argument to apply to the function
	 * @return the result of applying {@code argument} to {@code function}
	 * 
	 * @since 0.2.0
	 */
	public static <T, R> R apply(CheckedFunction<T, R> function, T argument)
	{
		try {
			return function.apply(argument);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * Convert a {@link CheckedBiFunction} into a {@link BiFunction}.
	 * 
	 * @param <T> first argument type
	 * @param <U> last argument type
	 * @param <R> return type
	 * 
	 * @param function the checked bi-function
	 * @return an unchecked bi-function wrapping the {@code function} argument.
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T, U, R> BiFunction<T, U, R> uncheckBiFunction(CheckedBiFunction<T, U, R> function)
	{
		return (BiFunction<T, U, R> & Serializable) (t, u) -> Exceptional.apply(function, t, u);
	}


	/**
	 * Invokes {@link CheckedFunction#apply(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> first argument type
	 * @param <U> last argument type
	 * @param <R> return type
	 * 
	 * @param function the checked function to invoke with the {@code argument}
	 * @param t the first argument to apply to the function
	 * @param u the second argument to apply to the function
	 * @return the result of applying {@code function} to the arguments {@code t} and {@code u}
	 * 
	 * @since 0.2.0
	 */
	public static <T, U, R> R apply(CheckedBiFunction<T, U, R> function, T t, U u)
	{
		try {
			return function.apply(t, u);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * Converts a {@link CheckedBinaryOperator} into a {@link BinaryOperator}.
	 * 
	 * @param <T> operator type
	 * 
	 * @param operator the checked binary operator
	 * @return and unchecked wrapper around the {@code operator} argument
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> BinaryOperator<T> uncheckBinaryOperator(CheckedBinaryOperator<T> operator)
	{
		return (BinaryOperator<T> & Serializable) (a, b) -> Exceptional.apply(operator, a, b);
	}


	/**
	 * Converts a {@link CheckedToDoubleFunction} into a {@link ToDoubleFunction}.
	 * 
	 * @param <T> argument type
	 * 
	 * @param function the checked to-double-function
	 * @return an unchecked to-double-function wrapping the {@code function} argument.
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> ToDoubleFunction<T> uncheckToDoubleFunction(CheckedToDoubleFunction<T> function)
	{
		return (ToDoubleFunction<T> & Serializable) t -> Exceptional.applyAsDouble(function, t);
	}


	/**
	 * Invokes {@link CheckedToDoubleFunction#applyAsDouble(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> argument type
	 * 
	 * @param function the checked to-double function
	 * @param t the function argument
	 * @return the double result of applying the {@code function} to argument {@code t}
	 * 
	 * @since 0.2.0
	 */
	public static <T> double applyAsDouble(CheckedToDoubleFunction<T> function, T t)
	{
		try {
			return function.applyAsDouble(t);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * Converts a {@link CheckedToIntFunction} into a {@link ToIntFunction}.
	 * 
	 * @param <T> argument type
	 * 
	 * @param function the checked to-int-function
	 * @return an unchecked to-int-function wrapping the {@code function} argument.
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> ToIntFunction<T> uncheckToIntFunction(CheckedToIntFunction<T> function)
	{
		return (ToIntFunction<T> & Serializable) t -> Exceptional.applyAsInt(function, t);
	}


	/**
	 * Invokes {@link CheckedToIntFunction#applyAsInt(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> argument type
	 * 
	 * @param function the checked to-int function
	 * @param t the function argument
	 * @return the int result of applying the {@code function} to argument {@code t}
	 * 
	 * @since 0.2.0
	 */
	public static <T> int applyAsInt(CheckedToIntFunction<T> function, T t)
	{
		try {
			return function.applyAsInt(t);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * Converts a {@link CheckedToLongFunction} into a {@link ToLongFunction}.
	 * 
	 * @param <T> argument type
	 * 
	 * @param function the checked to-long-function
	 * @return an unchecked to-long-function wrapping the {@code function} argument.
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> ToLongFunction<T> uncheckToLongFunction(CheckedToLongFunction<T> function)
	{
		return (ToLongFunction<T> & Serializable) t -> Exceptional.applyAsLong(function, t);
	}


	/**
	 * Invokes {@link CheckedToLongFunction#applyAsLong(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> argument type
	 * 
	 * @param function the checked to-long function
	 * @param t the function argument
	 * @return the long result of applying the {@code function} to argument {@code t}
	 * 
	 * @since 0.2.0
	 */
	public static <T> long applyAsLong(CheckedToLongFunction<T> function, T t)
	{
		try {
			return function.applyAsLong(t);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * Convert a {@link CheckedSupplier} into an unchecked {@link Supplier}.
	 * 
	 * @param <T> supplied type
	 * 
	 * @param supplier the checked supplier to wrap
	 * @return an unchecked supplier wrapping the {@code supplier} argument
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> Supplier<T> uncheckSupplier(CheckedSupplier<T> supplier)
	{
		return (Supplier<T> & Serializable) () -> Exceptional.get(supplier);
	}


	/**
	 * Invokes {@link CheckedSupplier#get()} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> supplied type
	 * 
	 * @param supplier the checked supplier
	 * @return the result as supplied from the {@code supplier} argument
	 * 
	 * @since 0.2.0
	 */
	public static <T> T get(CheckedSupplier<T> supplier)
	{
		try {
			return supplier.get();
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * Converts a {@link CheckedPredicate} into a {@link Predicate}.
	 * 
	 * @param <T> tested type
	 * 
	 * @param predicate the checked predicate to wrap
	 * @return an unchecked predicate wrapping the {@code predicate} argument
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> Predicate<T> uncheckPredicate(CheckedPredicate<T> predicate)
	{
		return (Predicate<T> & Serializable) t -> Exceptional.test(predicate, t);
	}


	/**
	 * Invokes {@link CheckedPredicate#test(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> tested type
	 * 
	 * @param predicate the checked predicate to test on the {@code value}
	 * @param value the value to be tested
	 * @return true IFF value passes predicate's test
	 * 
	 * @since 0.2.0
	 */
	public static <T> boolean test(CheckedPredicate<T> predicate, T value)
	{
		try {
			return predicate.test(value);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * Converts a {@link CheckedComparator} into an unchecked {@link Comparator}.
	 * 
	 * @param <T> compared type
	 * 
	 * @param comparator the checked comparator to wrap
	 * @return an unchecked comparator wrapping the {@code comparator} argument
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T> Comparator<T> uncheckComparator(CheckedComparator<T> comparator)
	{
		return (Comparator<T> & Serializable) (a, b) -> Exceptional.applyAsInt(comparator, a, b);
	}


	/**
	 * Converts a {@link CheckedToIntBiFunction} into an unchecked {@link ToIntBiFunction}.
	 * 
	 * @param <T> first argument type
	 * @param <U> last argument type
	 * 
	 * @param function the checked bi-function
	 * @return an unchecked bi-function
	 */
	@SuppressWarnings("squid:S1905") // SonarQube false positives
	public static <T, U> ToIntBiFunction<T, U> uncheckToIntBiFunction(CheckedToIntBiFunction<T, U> function)
	{
		return (ToIntBiFunction<T, U> & Serializable) (a, b) -> Exceptional.applyAsInt(function, a, b);
	}


	/**
	 * Invokes {@link CheckedFunction#apply(Object)} catching any checked
	 * {@link Exception}s rethrowing as unchecked.
	 * 
	 * @param <T> first argument type
	 * @param <U> last argument type
	 * 
	 * @param function the checked function to invoke with the {@code argument}
	 * @param t the first argument to apply to the function
	 * @param u the second argument to apply to the function
	 * @return the result of applying {@code function} to the arguments {@code t} and {@code u}
	 * 
	 * @since 0.2.0
	 */
	@SuppressWarnings("squid:S00112")  // necessary to declare Throwable
	public static <T, U> int applyAsInt(CheckedToIntBiFunction<T, U> function, T t, U u)
	{
		try {
			return function.applyAsInt(t, u);
		} catch(@SuppressWarnings("squid:S00112") Throwable thrown) {
			throw rethrow(thrown);
		}
	}


	/**
	 * <p>
	 * This fugly method relies on erasure to trick the compiler, allowing you to throw any checked
	 * exception without declaring so on the surrounding method. You are almost certainly better off
	 * using {@link #rethrow(Throwable)}.
	 * </p>
	 * <p>
	 * <b>Note</b>: this may well become an obsolete hack in future versions of Java if generics change.
	 * </p>
	 * 
	 * @param throwable the {@link Throwable} to be thrown
	 * @return this alleged return will never be received, but useful in that
	 * you may write <code>throws {@link Exceptional#throwAsUnchecked(Throwable)};</code>
	 * to placate the compiler WRT non-void method returns, and ensure Static Code
	 * Analysis doesn't accuse you of swallowing the exception.
	 * 
	 * @since 0.2.0
	 * 
	 * @see #rethrow(Throwable)
	 */
	public static RuntimeException throwAsUnchecked(Throwable throwable)
	{
		resetIfInterrupt(throwable);
		throw Exceptional.<RuntimeException> eraseAndThrow(throwable);
	}


	@SuppressWarnings("unchecked")
	private static <T extends Throwable> RuntimeException eraseAndThrow(Throwable throwable) throws T
	{
		throw (T) throwable;
	}


	/**
	 * <p>
	 * Attempts to unwrap invocation and unchecked exceptions to their underlying cause.
	 * Unwrapping is recursively applied. The original wrapper exception is added to the suppressed
	 * throwables of the returned unwrapped throwable.
	 * </p>
	 * 
	 * <p>
	 * Unwraps: {@link InvocationTargetException}, {@link UndeclaredThrowableException}, {@link UncheckedIOException}
	 * and {@link UncheckedException}
	 * </p>
	 * 
	 * @param throwable to be unwrapped
	 * @return the root cause, or original throwable if not unwrapped
	 * 
	 * @since 0.2.0
	 * 
	 */
	public static Throwable unwrap(Throwable throwable)
	{
		Throwable unwrapped = unwrapping(throwable);
		if(unwrapped != throwable) {
			unwrapped.addSuppressed(throwable);
		}
		return unwrapped;
	}


	private static Throwable unwrapping(Throwable throwable)
	{
		Set<Throwable> seen = new HashSet<>();
		while(isUnwrappable(throwable) && !seen.contains(throwable)) {
			seen.add(throwable);
			throwable = throwable.getCause();
		}
		return throwable;
	}


	private static boolean isUnwrappable(Throwable throwable)
	{
		return throwable.getCause() != null
				&& (throwable instanceof InvocationTargetException
						|| throwable instanceof UndeclaredThrowableException
						|| throwable instanceof UncheckedIOException
						|| throwable instanceof UncheckedException);
	}


	/**
	 * <p>
	 * Recursively find the wrapped throwable
	 * </p>
	 * 
	 * <b>NOTE</b>: this method now just delegates to {@link #unwrap(Throwable)} and is therefore superfluous,
	 * as such it will be removed in the next major release without impact to client code; given
	 * this, it is not marked {@link Deprecated} to avoid unnecessary upcasting to {@link Throwable}.
	 *
	 * @param throwable to be unwrapped
	 * @return the value of {@link UndeclaredThrowableException#getUndeclaredThrowable()}
	 * 
	 * @since 0.2.0
	 * 
	 * @see #unwrap(Throwable)
	 */
	public static Throwable unwrap(UndeclaredThrowableException throwable)
	{
		return unwrap((Throwable) throwable);
	}


	/**
	 * <p>
	 * Recursively find the wrapped throwable
	 * </p>
	 * 
	 * <b>NOTE</b>: this method now just delegates to {@link #unwrap(Throwable)} and is therefore superfluous,
	 * as such it will be removed in the next major release without impact to client code; given
	 * this, it is not marked {@link Deprecated} to avoid unnecessary upcasting to {@link Throwable}.
	 *
	 * @param throwable to be unwrapped
	 * @return the value of {@link InvocationTargetException#getTargetException()}
	 * 
	 * @since 0.2.0
	 */
	public static Throwable unwrap(InvocationTargetException throwable)
	{
		return unwrap((Throwable) throwable);
	}


	/**
	 * Ultra-shorthand for {@link AutoCloseable}/{@link java.io.Closeable}, obvious use for {@link java.io.InputStream}
	 * 
	 * @param <C> {@link AutoCloseable} type
	 * @param <T> {@code create} function argument type
	 * @param <R> the result type
	 * 
	 * @param create a function applying {@code t} to produce an {@link AutoCloseable} of type {@code <C>}
	 * @param t the argument to apply to the {@code create} function
	 * @param convert a function applied to the {@link AutoCloseable} to produce the result
	 * @return the result of applying the {@code convert} function
	 * 
	 * @since 0.2.0
	 * 
	 * @deprecated functionality moved to {@link Closing}
	 * @see Closing
	 */
	@Deprecated
	public static <C extends AutoCloseable, T, R> R closeAfterApplying(CheckedFunction<T, C> create, T t, CheckedFunction<C, R> convert)
	{
		return Closing.closeAfterApplying(create, t, convert);
	}


	/**
	 * Applies the function to the closeable, returning the result and closing the closable - checked exceptions are
	 * rethrown as unchecked.
	 * 
	 * @param <C> the auto-closeable type, to be created, consumed and closed
	 * @param <R> the result type
	 * 
	 * @param closeable the closeable subject of the {@code convert} function
	 * @param convert the function consuming the closeable and supplying the result
	 * @return the result of applying {@code convert} function to the {@code closeable} argument
	 * 
	 * @since 0.2.0
	 * 
	 * @deprecated functionality moved to {@link Closing#closeAfterApplying(AutoCloseable, CheckedFunction)}
	 * @see Closing
	 */
	@Deprecated
	public static <C extends AutoCloseable, R> R closeAfterApplying(C closeable, CheckedFunction<C, R> convert)
	{
		return Closing.closeAfterApplying(closeable, convert);
	}


	/**
	 * Applies the {@code create} function to {@code t}, resulting in a {@link AutoCloseable} which is closed after
	 * being consumed.
	 * Checked exceptions are rethrown as unchecked.
	 * 
	 * @param <C> the auto-closeable type, to be created, consumed and closed
	 * @param <T> the function's argument type, used to create the auto-closeable
	 * 
	 * @param create the function creating the {@link AutoCloseable}
	 * @param t the argument that the {@code create} function is applied to
	 * @param consume the consumer of the {@link AutoCloseable}
	 * 
	 * @since 0.2.0
	 * 
	 * @deprecated functionality moved to {@link Closing#closeAfterAccepting(CheckedFunction, Object, CheckedConsumer)}
	 * @see Closing
	 */
	@Deprecated
	public static <C extends AutoCloseable, T> void closeAfterAccepting(CheckedFunction<T, C> create, T t, CheckedConsumer<C> consume)
	{
		Closing.closeAfterAccepting(create, t, consume);
	}


	/**
	 * Consumes the {@code closeable} before closing. Checked exceptions are rethrown as unchecked.
	 * 
	 * @param <C> the auto-closeable type
	 * 
	 * @param closeable the closeable to be consumed and closed
	 * @param consume the consumer of the {@link AutoCloseable}
	 * 
	 * @since 0.2.0
	 * 
	 * @deprecated functionality moved to {@link Closing#closeAfterAccepting(AutoCloseable, CheckedConsumer)}
	 * @see Closing
	 */
	@Deprecated
	public static <C extends AutoCloseable> void closeAfterAccepting(C closeable, CheckedConsumer<C> consume)
	{
		Closing.closeAfterAccepting(closeable, consume);
	}


	/**
	 * Invokes the {@link Iterable#forEach(Consumer)} method having unchecked the consumer
	 * 
	 * @param <T> the consumed {@link Iterable}'s element type
	 * 
	 * @param iterable the Iterable to consume each element of
	 * @param consumer the checked consumer of the Iterable's elements
	 * 
	 * @since 0.4.0
	 * 
	 * @see #uncheckConsumer(CheckedConsumer)
	 */
	public static <T> void forEach(Iterable<T> iterable, CheckedConsumer<T> consumer)
	{
		iterable.forEach(uncheckConsumer(consumer));
	}


	/**
	 * Invokes the {@link Map#forEach(BiConsumer)} method having unchecked the bi-consumer
	 * 
	 * @param <K> the consumed {@link Map}'s key type
	 * @param <V> the consumed {@link Map}'s value type
	 * 
	 * @param map the entries to consume
	 * @param consumer the consumer of the map's key value pairs
	 * 
	 * @since 0.4.0
	 * 
	 * @see #uncheckBiConsumer(CheckedBiConsumer)
	 */
	public static <K, V> void forEach(Map<K, V> map, CheckedBiConsumer<K, V> consumer)
	{
		map.forEach(uncheckBiConsumer(consumer));
	}
}
