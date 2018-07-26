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

import static io.earcam.unexceptional.Exceptional.RETHROWING;
import static io.earcam.unexceptional.Exceptional.SWALLOWING;
import static io.earcam.unexceptional.Exceptional.apply;
import static io.earcam.unexceptional.Exceptional.call;
import static io.earcam.unexceptional.Exceptional.get;
import static io.earcam.unexceptional.Exceptional.run;
import static io.earcam.unexceptional.Exceptional.swallow;
import static io.earcam.unexceptional.Exceptional.throwAsUnchecked;
import static io.earcam.unexceptional.Exceptional.uncheckBiConsumer;
import static io.earcam.unexceptional.Exceptional.uncheckBiFunction;
import static io.earcam.unexceptional.Exceptional.uncheckBinaryOperator;
import static io.earcam.unexceptional.Exceptional.uncheckConsumer;
import static io.earcam.unexceptional.Exceptional.uncheckFunction;
import static io.earcam.unexceptional.Exceptional.uncheckIntConsumer;
import static io.earcam.unexceptional.Exceptional.uncheckPredicate;
import static io.earcam.unexceptional.Exceptional.uncheckRunnable;
import static io.earcam.unexceptional.Exceptional.uncheckSupplier;
import static io.earcam.unexceptional.Exceptional.uncheckToDoubleFunction;
import static io.earcam.unexceptional.Exceptional.uncheckToIntBiFunction;
import static io.earcam.unexceptional.Exceptional.uncheckToIntFunction;
import static io.earcam.unexceptional.Exceptional.uncheckToLongFunction;
import static io.earcam.unexceptional.Exceptional.unwrap;
import static io.earcam.unexceptional.Exceptional.uri;
import static io.earcam.unexceptional.Exceptional.url;
import static java.lang.Thread.currentThread;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExceptionalTest {

	@BeforeEach
	public void clearInterrupt()
	{
		while(Thread.interrupted());
	}


	private static int cmp(String a, String b)
	{
		return a.compareTo(b);
	}


	@Test
	public void cannotConstructWithoutObjenesis() throws Exception
	{
		Constructor<Exceptional> constructor = Exceptional.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			fail("should not reach here");
		} catch(InvocationTargetException e) {
			assertThat(unwrap(e), is(instanceOf(IllegalStateException.class)));
		}
	}

	@Nested
	public class Consumers {

		@Test
		public void uncheckedConsumerThrows()
		{
			Exception kaboom = new IOException();

			CheckedConsumer<String, ?> checked = (t) -> {
				throw kaboom;
			};
			Consumer<String> consumer = uncheckConsumer(checked);

			try {
				consumer.accept("oh yeah");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedConsumerThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedConsumer<String, ?> checked = (t) -> {
				throw kaboom;
			};
			Consumer<String> consumer = uncheckConsumer(checked);

			try {
				consumer.accept("oh yeah");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedConsumerDoesNotThrow()
		{
			final AtomicReference<String> accepted = new AtomicReference<String>("");
			CheckedConsumer<String, ?> checked = (t) -> {
				accepted.set(t);
			};

			Consumer<String> consumer = uncheckConsumer(checked);
			String passed = "oh noes";
			consumer.accept(passed);

			assertThat(accepted.get(), is(equalTo(passed)));
		}


		@Test
		public void uncheckedIntConsumerThrows()
		{
			Exception kaboom = new IOException();

			CheckedIntConsumer<?> checked = (t) -> {
				throw kaboom;
			};
			IntConsumer consumer = uncheckIntConsumer(checked);

			try {
				consumer.accept(101);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedIntConsumerThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedIntConsumer<?> checked = (t) -> {
				throw kaboom;
			};
			IntConsumer consumer = uncheckIntConsumer(checked);

			try {
				consumer.accept(101);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedIntConsumerDoesNotThrow()
		{
			final AtomicInteger accepted = new AtomicInteger(0);
			CheckedIntConsumer<?> checked = (t) -> {
				accepted.set(t);
			};

			IntConsumer consumer = uncheckIntConsumer(checked);
			int passed = 42;
			consumer.accept(passed);

			assertThat(accepted.get(), is(equalTo(passed)));
		}


		@Test
		public void uncheckedBiConsumerThrows()
		{
			Exception kaboom = new IOException();

			CheckedBiConsumer<String, String, ?> checked = (a, b) -> {
				throw kaboom;
			};
			BiConsumer<String, String> consumer = uncheckBiConsumer(checked);

			try {
				consumer.accept("oh", "noes");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedBiConsumerThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedBiConsumer<String, String, ?> checked = (a, b) -> {
				throw kaboom;
			};
			BiConsumer<String, String> consumer = uncheckBiConsumer(checked);

			try {
				consumer.accept("oh", "noes");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedBiConsumerDoesNotThrow()
		{
			final StringBuilder accepted = new StringBuilder();
			CheckedBiConsumer<String, String, ?> checked = (a, b) -> {
				accepted.append(a).append(b);
			};

			BiConsumer<String, String> consumer = uncheckBiConsumer(checked);
			consumer.accept("oh", "yeah");

			assertThat(accepted, hasToString(equalTo("ohyeah")));
		}
	}

	@Nested
	public class Suppliers {

		@Test
		public void uncheckedSupplierThrows()
		{
			Exception kaboom = new IOException();

			CheckedSupplier<String, ?> checked = () -> {
				throw kaboom;
			};
			Supplier<String> supplier = uncheckSupplier(checked);

			try {
				supplier.get();
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedSupplierThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedSupplier<String, ?> checked = () -> {
				throw kaboom;
			};
			Supplier<String> supplier = uncheckSupplier(checked);

			try {
				supplier.get();
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedSupplierDoesNotThrow()
		{
			final String supplied = "oh yeah";
			CheckedSupplier<String, ?> checked = () -> supplied;

			Supplier<String> unchecked = uncheckSupplier(checked);

			assertThat(unchecked.get(), is(equalTo(supplied)));
		}
	}

	@Nested
	public class Functions {

		@Test
		public void uncheckedBiFunctionThrows()
		{
			Exception kaboom = new IOException();

			CheckedBiFunction<String, String, Integer, ?> checked = (a, b) -> {
				throw kaboom;
			};
			BiFunction<String, String, Integer> func = uncheckBiFunction(checked);

			try {
				func.apply("oh", "noes");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedBiFunctionThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedBiFunction<String, String, Integer, ?> checked = (a, b) -> {
				throw kaboom;
			};
			BiFunction<String, String, Integer> func = uncheckBiFunction(checked);

			try {
				func.apply("oh", "noes");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedBiFunctionDoesNotThrow()
		{
			CheckedBiFunction<String, String, Integer, ?> checked = (a, b) -> a.length() + b.length();

			BiFunction<String, String, Integer> biFunction = uncheckBiFunction(checked);
			Integer applied = biFunction.apply("oh", "yeah");

			assertThat(applied, is(equalTo(6)));
		}


		@Test
		public void uncheckedBinaryOperatorThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedBinaryOperator<Integer, ?> checked = (a, b) -> {
				throw kaboom;
			};
			BinaryOperator<Integer> operator = uncheckBinaryOperator(checked);

			try {
				operator.apply(2, 2);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedPredicateDoesNotThrow()
		{
			CheckedPredicate<Integer, ?> isEven = i -> i % 2 == 0;

			Predicate<Integer> predicate = uncheckPredicate(isEven);

			assertThat(predicate.test(2), is(true));
		}


		@Test
		public void uncheckedPredicateThrows()
		{
			Exception kaboom = new IOException();

			CheckedPredicate<Integer, ?> checked = i -> {
				throw kaboom;
			};
			Predicate<Integer> predicate = uncheckPredicate(checked);

			try {
				predicate.test(2);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedPredicateThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedPredicate<Integer, ?> checked = i -> {
				throw kaboom;
			};
			Predicate<Integer> predicate = uncheckPredicate(checked);

			try {
				predicate.test(2);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedBinaryOperatorDoesNotThrow()
		{
			CheckedBinaryOperator<Integer, ?> add = (a, b) -> a + b;

			BinaryOperator<Integer> op = uncheckBinaryOperator(add);
			Integer applied = op.apply(2, 2);

			assertThat(applied, is(equalTo(4)));
		}


		@Test
		public void uncheckedFunctionThrows()
		{
			Exception kaboom = new IOException();

			CheckedFunction<String, Integer, ?> checked = a -> {
				throw kaboom;
			};
			Function<String, Integer> func = uncheckFunction(checked);

			try {
				func.apply("oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedFunctionThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedFunction<String, Integer, ?> checked = a -> {
				throw kaboom;
			};
			Function<String, Integer> func = uncheckFunction(checked);

			try {
				func.apply("oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedFunctionDoesNotThrow()
		{
			CheckedFunction<String, Integer, ?> checked = Integer::parseInt;

			Function<String, Integer> function = uncheckFunction(checked);
			Integer applied = function.apply("42");

			assertThat(applied, is(equalTo(42)));
		}


		@Test
		public void uncheckedToIntFunctionThrows()
		{
			Exception kaboom = new IOException();

			CheckedToIntFunction<String, ?> checked = a -> {
				throw kaboom;
			};
			ToIntFunction<String> func = uncheckToIntFunction(checked);

			try {
				func.applyAsInt("oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedToIntFunctionThrowsError()
		{
			Error kaboom = new Error();

			CheckedToIntFunction<String, ?> checked = a -> {
				throw kaboom;
			};

			try {
				Exceptional.applyAsInt(checked, "oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedToIntFunctionDoesNotThrow()
		{
			CheckedToIntFunction<String, ?> checked = Integer::parseInt;

			ToIntFunction<String> function = uncheckToIntFunction(checked);
			int applied = function.applyAsInt("42");

			assertThat(applied, is(equalTo(42)));
		}


		@Test
		public void uncheckedToDoubleFunctionThrows()
		{
			Exception kaboom = new IOException();

			CheckedToDoubleFunction<String, ?> checked = a -> {
				throw kaboom;
			};
			ToDoubleFunction<String> func = uncheckToDoubleFunction(checked);

			try {
				func.applyAsDouble("oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedToDoubleFunctionThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedToDoubleFunction<String, ?> checked = a -> {
				throw kaboom;
			};
			ToDoubleFunction<String> func = uncheckToDoubleFunction(checked);

			try {
				func.applyAsDouble("oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedToDoubleFunctionDoesNotThrow()
		{
			CheckedToDoubleFunction<String, ?> checked = Double::parseDouble;

			ToDoubleFunction<String> function = uncheckToDoubleFunction(checked);
			double applied = function.applyAsDouble("42");

			assertThat(applied, is(equalTo(42D)));
		}


		@Test
		public void uncheckedToLongFunctionThrows()
		{
			Exception kaboom = new IOException();

			CheckedToLongFunction<String, ?> checked = a -> {
				throw kaboom;
			};
			ToLongFunction<String> func = uncheckToLongFunction(checked);

			try {
				func.applyAsLong("oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedToLongFunctionThrowsError()
		{
			Throwable kaboom = new Error();

			CheckedToLongFunction<String, ?> checked = a -> {
				throw kaboom;
			};
			ToLongFunction<String> func = uncheckToLongFunction(checked);

			try {
				func.applyAsLong("oops");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(kaboom)));
			}
		}


		@Test
		public void uncheckedToLongFunctionDoesNotThrow()
		{
			CheckedToLongFunction<String, ?> checked = Long::parseLong;

			ToLongFunction<String> function = uncheckToLongFunction(checked);
			long applied = function.applyAsLong("42");

			assertThat(applied, is(equalTo(42L)));
		}


		@Test
		public void nothingIsThrownWhenRunnableExecutesNormally()
		{
			AtomicBoolean executed = new AtomicBoolean(false);

			run(() -> executed.set(true));

			assertThat(executed.get(), is(true));
		}


		@Test
		public void nothingIsThrownWhenCallableExecutesNormally()
		{
			final int value = 42;

			Integer result = get(() -> value);

			assertThat(result, is(equalTo(value)));
		}


		@Test
		public void rethrowsRuntimeExceptionFromCallable()
		{
			final IllegalArgumentException thrown = new IllegalArgumentException("noo");
			try {
				Callable<String> action = () -> {
					throw thrown;
				};
				call(action);
				fail("should not reach here");
			} catch(Exception caught) {
				assertThat(caught, is(sameInstance(thrown)));
			}
		}


		@Test
		public void wrapsIOExceptionFromCallable()
		{
			final IOException thrown = new IOException("noo");
			try {
				Callable<String> action = () -> {
					throw thrown;
				};
				call(action);
				fail("should not reach here");
			} catch(Exception caught) {
				assertThat(caught, is(instanceOf(UncheckedIOException.class)));
				assertThat(caught.getCause(), is(sameInstance(thrown)));
			}
		}


		@Test
		public void wrapsReflectiveExceptionFromCallable()
		{
			final InstantiationException thrown = new InstantiationException("noo");
			try {
				Callable<String> action = () -> {
					throw thrown;
				};
				call(action);
				fail("should not reach here");
			} catch(Exception caught) {
				assertThat(caught, is(instanceOf(UncheckedReflectiveException.class)));
				assertThat(caught.getCause(), is(sameInstance(thrown)));
			}
		}


		@Test
		public void wrapsSecurityExceptionFromCheckedRunable()
		{
			final KeyManagementException thrown = new KeyManagementException("noo");
			try {
				CheckedRunnable<?> action = () -> {
					throw thrown;
				};
				run(action);
				fail("should not reach here");
			} catch(Exception caught) {
				assertThat(caught, is(instanceOf(UncheckedSecurityException.class)));
				assertThat(caught.getCause(), is(sameInstance(thrown)));
			}
		}


		@Test
		public void wrapsExceptionFromCheckedRunable()
		{
			final Exception thrown = new Exception("noo");
			try {
				CheckedRunnable<?> action = () -> {
					throw thrown;
				};
				run(action);
				fail("should not reach here");
			} catch(Exception caught) {
				assertThat(caught, is(instanceOf(UncheckedException.class)));
				assertThat(caught.getCause(), is(sameInstance(thrown)));
			}
		}


		@Test
		public void callableUncheckedThrows()
		{
			CertificateException e = new CertificateException();
			Callable<String> callable = () -> {
				throw e;
			};

			try {
				call(callable);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedSecurityException.class)));
				assertThat(thrown.getCause(), is(sameInstance(e)));
			}
		}


		@Test
		public void callableUncheckedThrowsError()
		{
			Error e = new Error();
			Callable<String> callable = () -> {
				throw e;
			};

			try {
				call(callable);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(e)));
			}
		}


		@Test
		public void runnableUncheckedThrows()
		{
			IllegalAccessException e = new IllegalAccessException();
			CheckedRunnable<?> checked = () -> {
				throw e;
			};

			Runnable unchecked = uncheckRunnable(checked);
			try {
				unchecked.run();
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedReflectiveException.class)));
				assertThat(thrown.getCause(), is(sameInstance(e)));
			}
		}


		@Test
		public void runnableUncheckedThrowsError()
		{
			Throwable e = new Error();
			CheckedRunnable<?> checked = () -> {
				throw e;
			};

			Runnable unchecked = uncheckRunnable(checked);
			try {
				unchecked.run();
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(e)));
			}
		}


		@Test
		public void callableUncheckedCalled()
		{
			final String value = "hello there, how's tricks?";

			Callable<String> callable = () -> value;

			String called = call(callable);

			assertThat(called, is(equalTo(value)));
		}


		@Test
		public void runnableUncheckedRan()
		{
			final AtomicBoolean ran = new AtomicBoolean(false);

			CheckedRunnable<?> runnable = () -> ran.set(true);

			run(runnable);

			assertThat(ran.get(), is(true));
		}


		@Test
		public void functionApplied()
		{
			CheckedFunction<String, Integer, ?> function = Integer::parseInt;

			Integer fortyTwo = apply(function, "42");

			assertThat(fortyTwo, is(42));
		}


		@Test
		public void functionThrowsUnchecked()
		{
			CheckedFunction<String, Integer, ?> function = Integer::parseInt;

			try {
				apply(function, "forty two");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(NumberFormatException.class)));
			}
		}


		@Test
		public void uncheckToIntBiFunctionThrows()
		{
			ToIntBiFunction<String, String> fn = uncheckToIntBiFunction(ExceptionalTest::cmp);
			try {
				fn.applyAsInt(null, null);
				fail("should not reach here");
			} catch(NullPointerException npe) {}
		}


		@Test
		public void uncheckToIntBiFunctionThrowsError()
		{
			Error error = new Error();
			ToIntBiFunction<String, String> fn = uncheckToIntBiFunction((a, b) -> {
				throw error;
			});
			try {
				fn.applyAsInt("a", "b");
				fail("should not reach here");
			} catch(Error e) {
				assertThat(e, is(sameInstance(error)));
			}
		}


		@Test
		public void uncheckToIntBiFunctionInvoked()
		{
			ToIntBiFunction<String, String> fn = uncheckToIntBiFunction(ExceptionalTest::cmp);
			int cmp = fn.applyAsInt("a", "a");
			assertThat(cmp, is(0));
		}
	}


	@Test
	public void throwCheckedAsUnchecked()
	{
		IOException ioe = new IOException("check this out");
		try {
			throwAsUnchecked(ioe);
			fail("should not reach here");
		} catch(Throwable thrown) {
			assertThat(thrown, is(sameInstance(ioe)));
		}
	}

	@Nested
	public class Unwrapped {
		@SuppressWarnings("unchecked")
		@Test
		public void unwrapCheckedInvocationException()
		{
			InvocationHandler handler = (proxy, method, args) -> {
				throw new IOException("Computer says no");
			};

			Comparable<Object> proxy = createComparableProxy(handler);

			try {
				proxy.compareTo(new Object());
				fail("should not reach here");
			} catch(Throwable t) {
				Throwable unwrapped = unwrap(t);
				assertThat(unwrapped, instanceOf(IOException.class));
				assertThat(unwrapped.getSuppressed(), arrayContaining(instanceOf(UndeclaredThrowableException.class)));
			}
		}


		@SuppressWarnings("unchecked")
		private Comparable<Object> createComparableProxy(InvocationHandler handler)
		{
			return (Comparable<Object>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Comparable.class }, handler);
		}


		@Test
		public void nothingToUnwrapForUncheckedInvocationException()
		{
			InvocationHandler handler = (proxy, method, args) -> {
				throw new IllegalStateException("Computer says no");
			};

			Comparable<Object> proxy = createComparableProxy(handler);

			try {
				proxy.compareTo(new Object());
				fail("should not reach here");
			} catch(Throwable t) {
				Throwable unwrapped = unwrap(t);
				assertThat(unwrapped, instanceOf(IllegalStateException.class));
				assertThat(unwrapped, is(equalTo(t)));
			}
		}


		@Test
		public void unwrapReflectiveOperationException()
		{
			class BadlyBehaved {
				@SuppressWarnings("unused")
				public void seeminglyInnocuous()
				{
					throw new IllegalArgumentException("I don't declare any args anyway");
				}
			}

			BadlyBehaved bad = new BadlyBehaved();

			try {
				BadlyBehaved.class.getMethod("seeminglyInnocuous").invoke(bad);
				fail("should not reach here");
			} catch(Throwable thrown) {
				Throwable unwrap = unwrap(thrown);

				assertThat(thrown, is(instanceOf(InvocationTargetException.class)));
				assertThat(unwrap, is(instanceOf(IllegalArgumentException.class)));
				assertThat(unwrap.getSuppressed(), arrayContaining(thrown));
			}
		}


		@Test
		public void unwrapUndeclaredThrowableException()
		{
			Throwable expected = new RuntimeException("oops");
			UndeclaredThrowableException thrown = new UndeclaredThrowableException(expected);

			Throwable unwrap = unwrap(thrown);

			assertThat(unwrap, is(expected));
			assertThat(unwrap.getSuppressed(), arrayContaining(thrown));
		}


		@Test
		public void unwrapUncheckedIOException()
		{
			IOException expected = new IOException("io io, it's off to work we go");
			Throwable thrown = new UncheckedIOException(expected);

			Throwable unwrap = unwrap(thrown);

			assertThat(unwrap, is(expected));
			assertThat(unwrap.getSuppressed(), arrayContaining(thrown));
		}


		@Test
		public void unwrapUncheckedException()
		{
			IllegalStateException expected = new IllegalStateException();
			Throwable thrown = new UncheckedException(expected);

			Throwable unwrap = unwrap(thrown);

			assertThat(unwrap, is(expected));
			assertThat(unwrap.getSuppressed(), arrayContaining(thrown));
		}


		@Test
		public void unwrapChain()
		{
			IllegalStateException ise = new IllegalStateException();
			InvocationTargetException ite = new InvocationTargetException(ise);
			UndeclaredThrowableException ute = new UndeclaredThrowableException(ite);
			UncheckedException ue = new UncheckedException(ute);

			Throwable unwrap = unwrap(ue);

			assertThat(unwrap, is(ise));

			assertThat(unwrap.getCause(), is(nullValue()));
			assertThat(unwrap.getSuppressed(), is(arrayContaining(ue)));
		}


		@Test
		public void unUnwrappableWhenNoCause()
		{
			UncheckedException ue = new UncheckedException("no cause for concern");

			assertThat(unwrap(ue), is(sameInstance(ue)));
		}


		@Test
		public void unUnwrappableWhenIrrelevant()
		{
			Exception e = new Exception(new NullPointerException());
			assertThat(unwrap(e), is(sameInstance(e)));
		}


		@Test
		public void unwrapChainWithCircularReference()
		{
			UncheckedException ue = new UncheckedException("");
			InvocationTargetException ite = new InvocationTargetException(ue);
			UndeclaredThrowableException ute = new UndeclaredThrowableException(ite);
			UncheckedException outer = new UncheckedException(ite);

			ue.initCause(ute);

			Throwable unwrapped = unwrap(outer);

			assertThat(unwrapped, is(ite));
		}
	}

	@Nested
	public class Interrupt {
		@Test
		public void resetsInterruptException()
		{
			assumeFalse(currentThread().isInterrupted());
			try {
				get(() -> {
					throw new InterruptedException("Excuse me");
				});
				fail("should not reach here");
			} catch(Exception e) {
				assertThat(e, instanceOf(UncheckedInterruptException.class));
				assertThat(e.getCause(), instanceOf(InterruptedException.class));
				assertThat(currentThread().isInterrupted(), is(true));
			}
		}


		@Test
		public void swallowResetsInterruptException()
		{
			assumeFalse(currentThread().isInterrupted());
			try {
				throw new InterruptedException("Pardon me");
			} catch(Exception e) {
				swallow(e);
			}
			assertThat(currentThread().isInterrupted(), is(true));
		}


		@Test
		public void swallowRethrowsErrors()
		{
			OutOfMemoryError oom = new OutOfMemoryError("Not really");
			try {
				swallow(oom);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(oom)));
			}
		}


		@Test
		public void doesNotSetInterrupt()
		{
			assumeFalse(currentThread().isInterrupted());

			try {
				CheckedRunnable<?> executable = () -> {
					throw new ReflectiveOperationException("Think back");
				};
				run(executable);
				fail("should not reach here");
			} catch(Exception e) {
				assertThat(e, instanceOf(UncheckedReflectiveException.class));
				assertThat(currentThread().isInterrupted(), is(false));
			}
		}


		@Test
		public void throwCheckedResetsInterrupt()
		{
			try {
				throwAsUnchecked(new InterruptedException("Terribly sorry to butt in, but..."));
				fail("should not reach here");
			} catch(Exception e) {}
			assertThat(Thread.currentThread().isInterrupted(), is(true));
		}
	}

	@Nested
	public class Earl {

		@Test
		public void validUrlString()
		{
			String earl = "http://example.com/which/path/to/persia?they-never-mention=peace&love";
			URL url = url(earl);

			assertThat(url.toString(), is(equalTo(earl)));
		}


		@Test
		public void invalidUrlStringThrownAsUnchecked()
		{
			try {
				url("http ://bad/protocol");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(instanceOf(MalformedURLException.class)));
			}
		}


		@Test
		public void validUrl()
		{
			String path = "/the/road/of/excess/leads/to/the/palace/of/wisdom";
			String earl = "https://williamblake.com:8443" + path;

			URL url = url("https", "williamblake.com", 8443, path, null);

			assertThat(url.toString(), is(equalTo(earl)));
		}


		@Test
		public void validUrlToUri() throws MalformedURLException
		{
			String earl = "https://williamblake.com:8443/the/road/of/excess/leads/to/the/palace/of/wisdom";

			URI uri = Exceptional.uri(new URL(earl));

			assertThat(uri.toString(), is(equalTo(earl)));
		}


		@Test
		public void invalidUrlToUri() throws MalformedURLException
		{
			URL earl = new URL("http:// ");
			try {
				Exceptional.uri(earl);
				fail("should not reach here");
			} catch(UncheckedException e) {}
		}


		@Test
		public void invalidUrlThrownAsUnchecked()
		{
			try {
				url("gobbledygook", "horrid-books.com", 80, "/which/path/to/persia?they-never-mention=peace&love", null);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedIOException.class)));
				assertThat(thrown.getCause(), is(instanceOf(MalformedURLException.class)));
			}
		}


		@Test
		public void validUri()
		{
			String yuri = "mailto:bob@fe.tt";
			URI uri = uri(yuri);

			assertThat(uri.toString(), is(equalTo(yuri)));
		}


		@Test
		public void invalidUriThrownAsUnchecked()
		{
			try {
				uri("http://this is not a valid URI");
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(instanceOf(UncheckedUriSyntaxException.class)));
				assertThat(thrown.getCause(), is(instanceOf(URISyntaxException.class)));
			}
		}
	}

	@Nested
	public class Handler {

		@Test
		public void rethrowingUncaughtExceptionHandlerRethrows()
		{
			NullPointerException exception = new NullPointerException();

			try {
				RETHROWING.uncaughtException(currentThread(), exception);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(exception)));
			}
		}


		@Test
		public void rethrowingUncaughtExceptionHandlerRethrowsWrappedCheckedException()
		{
			IOException exception = new IOException();

			try {
				RETHROWING.uncaughtException(currentThread(), exception);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown.getCause(), is(sameInstance(exception)));
			}
		}


		@Test
		public void swallowingUncaughtExceptionHandlerSwallowsRuntimeExceptions()
		{
			SWALLOWING.uncaughtException(currentThread(), new NullPointerException());
		}


		@Test
		public void swallowingUncaughtExceptionHandlerRethrowsErrors()
		{
			ThreadDeath error = new ThreadDeath();

			try {
				SWALLOWING.uncaughtException(currentThread(), error);
				fail("should not reach here");
			} catch(Throwable thrown) {
				assertThat(thrown, is(sameInstance(error)));
			}
		}
	}

	@Nested
	public class ForEach {

		@Test
		public void iterableForEachThrows()
		{
			IOException curveBall = new IOException();
			try {
				Exceptional.forEach(Collections.singleton("bang?"), s -> {
					throw curveBall;
				});
				fail("should not reach here");
			} catch(UncheckedIOException e) {
				assertThat(e.getCause(), is(sameInstance(curveBall)));
			}
		}


		@Test
		public void iterableForEach()
		{
			Iterable<String> source = Arrays.asList("Fee", "fi", "fo", "fum");
			List<String> sink = new ArrayList<>(4);

			Exceptional.forEach(source, sink::add);

			assertThat(sink, is(equalTo(source)));
		}


		@Test
		public void mapForEachThrows()
		{
			IOException curveBall = new IOException();
			try {
				Exceptional.forEach(Collections.singletonMap("crash?", "bang?"), (k, v) -> {
					throw curveBall;
				});
				fail("should not reach here");
			} catch(UncheckedIOException e) {
				assertThat(e.getCause(), is(sameInstance(curveBall)));
			}
		}


		@Test
		public void mapForEach()
		{
			Map<String, String> source = Collections.singletonMap("crash?", "bang?");
			Map<String, String> sink = new HashMap<>();

			Exceptional.forEach(source, sink::put);

			assertThat(sink, is(equalTo(source)));
		}
	}

	@Nested
	public class Rethrow {

		@Test
		public void rethrowDoesNotWrapRuntimeException()
		{
			UncheckedIOException uie = new UncheckedIOException(new IOException());
			try {
				Exceptional.rethrow(uie);
				fail("should not reach here");
			} catch(UncheckedIOException caught) {
				assertThat(caught, is(sameInstance(uie)));
			}
		}


		@Test
		public void rethrowDoesNotWrapError()
		{
			OutOfMemoryError oom = new OutOfMemoryError();
			try {
				Exceptional.rethrow(oom);
				fail("should not reach here");
			} catch(OutOfMemoryError caught) {
				assertThat(caught, is(sameInstance(oom)));
			}
		}
	}
}
