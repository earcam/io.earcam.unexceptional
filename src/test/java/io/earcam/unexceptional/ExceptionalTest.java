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

import static io.earcam.unexceptional.Exceptional.*;
import static java.lang.Thread.currentThread;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExceptionalTest {

	private static final class ThrowsAtClosingTime implements Closeable {

		private IOException exception;


		public ThrowsAtClosingTime(IOException exception)
		{
			this.exception = exception;
		}


		@Override
		public void close() throws IOException
		{
			throw exception;
		}
	};

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void clearInterrupt()
	{
		while(Thread.interrupted());
	}


	@Test
	public void resetsInterruptException()
	{
		assumeFalse(currentThread().isInterrupted());
		try {
			get(() -> {
				throw new InterruptedException("Excuse me");
			});
			fail();
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
		thrown.expect(sameInstance(oom));

		swallow(oom);
	}


	@Test
	public void doesNotSetInterrupt()
	{
		assumeFalse(currentThread().isInterrupted());

		try {
			CheckedRunnable executable = () -> {
				throw new ReflectiveOperationException("Think back");
			};
			run(executable);
			fail();
		} catch(Exception e) {
			assertThat(e, instanceOf(UncheckedReflectiveException.class));
			assertThat(currentThread().isInterrupted(), is(false));
		}
	}


	@Test
	public void throwCheckedAsUnchecked()
	{
		IOException ioe = new IOException("check this out");
		thrown.expect(is(ioe));

		throwAsUnchecked(ioe);
	}


	@Test
	public void throwCheckedResetsInterrupt()
	{
		try {
			throwAsUnchecked(new InterruptedException("Terribly sorry to butt in, but..."));
			fail();
		} catch(Exception e) {}
		assertThat(Thread.currentThread().isInterrupted(), is(true));
	}


	@Test
	public void uncheckedConsumerThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedConsumer<String> checked = (t) -> {
			throw kaboom;
		};
		Consumer<String> consumer = uncheckConsumer(checked);

		consumer.accept("oh yeah");
	}


	@Test
	public void uncheckedConsumerDoesNotThrow()
	{
		final AtomicReference<String> accepted = new AtomicReference<String>("");
		CheckedConsumer<String> checked = (t) -> {
			accepted.set(t);
		};

		Consumer<String> consumer = uncheckConsumer(checked);
		String passed = "oh noes";
		consumer.accept(passed);

		assertThat(accepted.get(), is(equalTo(passed)));
	}


	@Test
	public void uncheckedSupplierThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedSupplier<String> checked = () -> {
			throw kaboom;
		};
		Supplier<String> consumer = uncheckSupplier(checked);

		consumer.get();
	}


	@Test
	public void uncheckedSupplierDoesNotThrow()
	{
		final String supplied = "oh yeah";
		CheckedSupplier<String> checked = () -> supplied;

		Supplier<String> unchecked = uncheckSupplier(checked);

		assertThat(unchecked.get(), is(equalTo(supplied)));
	}


	@Test
	public void uncheckedBiConsumerThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedBiConsumer<String, String> checked = (a, b) -> {
			throw kaboom;
		};
		BiConsumer<String, String> consumer = uncheckBiConsumer(checked);

		consumer.accept("oh", "noes");
	}


	@Test
	public void uncheckedBiConsumerDoesNotThrow()
	{
		final StringBuilder accepted = new StringBuilder();
		CheckedBiConsumer<String, String> checked = (a, b) -> {
			accepted.append(a).append(b);
		};

		BiConsumer<String, String> consumer = uncheckBiConsumer(checked);
		consumer.accept("oh", "yeah");

		assertThat(accepted, hasToString(equalTo("ohyeah")));
	}


	@Test
	public void uncheckedBiFunctionThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedBiFunction<String, String, Integer> checked = (a, b) -> {
			throw kaboom;
		};
		BiFunction<String, String, Integer> func = uncheckBiFunction(checked);

		func.apply("oh", "noes");
	}


	@Test
	public void uncheckedBiFunctionDoesNotThrow()
	{
		CheckedBiFunction<String, String, Integer> checked = (a, b) -> a.length() + b.length();

		BiFunction<String, String, Integer> biFunction = uncheckBiFunction(checked);
		Integer applied = biFunction.apply("oh", "yeah");

		assertThat(applied, is(equalTo(6)));
	}


	@Test
	public void uncheckedBinaryOperatorThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedBinaryOperator<Integer> checked = (a, b) -> {
			throw kaboom;
		};
		BinaryOperator<Integer> consumer = uncheckBinaryOperator(checked);

		consumer.apply(2, 2);
	}


	@Test
	public void uncheckedPredicateDoesNotThrow()
	{
		CheckedPredicate<Integer> isEven = i -> i % 2 == 0;

		Predicate<Integer> predicate = uncheckPredicate(isEven);

		assertThat(predicate.test(2), is(true));
	}


	@Test
	public void uncheckedPredicateThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedPredicate<Integer> checked = i -> {
			throw kaboom;
		};
		Predicate<Integer> predicate = uncheckPredicate(checked);

		predicate.test(2);
	}


	@Test
	public void uncheckedBinaryOperatorDoesNotThrow()
	{
		CheckedBinaryOperator<Integer> add = (a, b) -> a + b;

		BinaryOperator<Integer> op = uncheckBinaryOperator(add);
		Integer applied = op.apply(2, 2);

		assertThat(applied, is(equalTo(4)));
	}


	@Test
	public void uncheckedFunctionThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedFunction<String, Integer> checked = a -> {
			throw kaboom;
		};
		Function<String, Integer> func = uncheckFunction(checked);

		func.apply("oops");
	}


	@Test
	public void uncheckedFunctionDoesNotThrow()
	{
		CheckedFunction<String, Integer> checked = Integer::parseInt;

		Function<String, Integer> function = uncheckFunction(checked);
		Integer applied = function.apply("42");

		assertThat(applied, is(equalTo(42)));
	}


	@Test
	public void uncheckedToIntFunctionThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedToIntFunction<String> checked = a -> {
			throw kaboom;
		};
		ToIntFunction<String> func = uncheckToIntFunction(checked);

		func.applyAsInt("oops");
	}


	@Test
	public void uncheckedToIntFunctionDoesNotThrow()
	{
		CheckedToIntFunction<String> checked = Integer::parseInt;

		ToIntFunction<String> function = uncheckToIntFunction(checked);
		int applied = function.applyAsInt("42");

		assertThat(applied, is(equalTo(42)));
	}


	@Test
	public void uncheckedToDoubleFunctionThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedToDoubleFunction<String> checked = a -> {
			throw kaboom;
		};
		ToDoubleFunction<String> func = uncheckToDoubleFunction(checked);

		func.applyAsDouble("oops");
	}


	@Test
	public void uncheckedToDoubleFunctionDoesNotThrow()
	{
		CheckedToDoubleFunction<String> checked = Double::parseDouble;

		ToDoubleFunction<String> function = uncheckToDoubleFunction(checked);
		double applied = function.applyAsDouble("42");

		assertThat(applied, is(equalTo(42D)));
	}


	@Test
	public void uncheckedToLongFunctionThrows()
	{
		Exception kaboom = new IOException();
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(sameInstance(kaboom)));

		CheckedToLongFunction<String> checked = a -> {
			throw kaboom;
		};
		ToLongFunction<String> func = uncheckToLongFunction(checked);

		func.applyAsLong("oops");
	}


	@Test
	public void uncheckedToLongFunctionDoesNotThrow()
	{
		CheckedToLongFunction<String> checked = Long::parseLong;

		ToLongFunction<String> function = uncheckToLongFunction(checked);
		long applied = function.applyAsLong("42");

		assertThat(applied, is(equalTo(42L)));
	}


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
			fail();
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
			fail();
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
			fail();
		} catch(Throwable thrown) {
			Throwable unwrap = unwrap(thrown);

			assertThat(thrown, is(instanceOf(InvocationTargetException.class)));
			assertThat(unwrap, is(instanceOf(IllegalArgumentException.class)));
			assertThat(unwrap.getSuppressed(), arrayContaining(thrown));
		}
	}


	@Test
	public void unwrapUncheckedException()
	{
		IllegalStateException expected = new IllegalStateException();

		try {
			throw new UncheckedException(expected);
		} catch(Throwable thrown) {
			Throwable unwrap = unwrap(thrown);

			assertThat(unwrap, is(expected));
			assertThat(unwrap.getSuppressed(), arrayContaining(thrown));
		}
	}


	@Test
	public void unwrapChain()
	{
		IllegalStateException ise = new IllegalStateException();
		InvocationTargetException ite = new InvocationTargetException(ise);
		UndeclaredThrowableException ute = new UndeclaredThrowableException(ite);
		UncheckedException ue = new UncheckedException(ute);

		try {
			throw ue;
		} catch(Throwable thrown) {
			Throwable unwrap = unwrap(thrown);

			assertThat(unwrap, is(ise));
			assertThat(unwrap.getSuppressed(), arrayContainingInAnyOrder(ite, ute, ue));
		}
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
	public void cannotConstructWithoutObjenesis() throws Exception
	{
		Constructor<Exceptional> constructor = Exceptional.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			fail();
		} catch(InvocationTargetException e) {
			assertThat(unwrap(e), is(instanceOf(IllegalStateException.class)));
		}
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
			fail();
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
			fail();
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
			fail();
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
			CheckedRunnable action = () -> {
				throw thrown;
			};
			run(action);
			fail();
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
			CheckedRunnable action = () -> {
				throw thrown;
			};
			run(action);
			fail();
		} catch(Exception caught) {
			assertThat(caught, is(instanceOf(UncheckedException.class)));
			assertThat(caught.getCause(), is(sameInstance(thrown)));
		}
	}


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
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(instanceOf(MalformedURLException.class)));

		url("http ://bad/protocol");
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
	public void invalidUrlThrownAsUnchecked()
	{
		thrown.expect(is(instanceOf(UncheckedIOException.class)));
		thrown.expectCause(is(instanceOf(MalformedURLException.class)));

		url("gobbledygook", "horrid-books.com", 80, "/which/path/to/persia?they-never-mention=peace&love", null);
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
		thrown.expect(is(instanceOf(UncheckedException.class)));
		thrown.expectCause(is(instanceOf(URISyntaxException.class)));

		uri("http://this is not a valid URI");
	}


	@Test
	public void rethrowingUncaughtExceptionHandlerRethrows()
	{
		NullPointerException exception = new NullPointerException();
		thrown.expect(sameInstance(exception));

		RETHROWING.uncaughtException(currentThread(), exception);
	}


	@Test
	public void rethrowingUncaughtExceptionHandlerRethrowsWrappedCheckedException()
	{
		IOException exception = new IOException();
		thrown.expect(UncheckedIOException.class);
		thrown.expectCause(sameInstance(exception));

		RETHROWING.uncaughtException(currentThread(), exception);
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
		thrown.expect(sameInstance(error));

		SWALLOWING.uncaughtException(currentThread(), error);
	}


	@Test
	public void callableUncheckedThrows()
	{
		CertificateException ioe = new CertificateException();
		Callable<String> callable = () -> {
			throw ioe;
		};

		thrown.expect(UncheckedSecurityException.class);
		thrown.expectCause(sameInstance(ioe));

		call(callable);
	}


	@Test
	public void runnableUncheckedThrows()
	{
		IllegalAccessException iae = new IllegalAccessException();
		CheckedRunnable checked = () -> {
			throw iae;
		};

		thrown.expect(UncheckedReflectiveException.class);
		thrown.expectCause(sameInstance(iae));

		Runnable unchecked = uncheckRunnable(checked);
		unchecked.run();
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

		CheckedRunnable runnable = () -> ran.set(true);

		run(runnable);

		assertThat(ran.get(), is(true));
	}


	@Test
	public void functionApplied()
	{
		CheckedFunction<String, Integer> function = Integer::parseInt;

		Integer fortyTwo = apply(function, "42");

		assertThat(fortyTwo, is(42));
	}


	@Test
	public void functionThrowsUnchecked()
	{
		thrown.expect(NumberFormatException.class);

		CheckedFunction<String, Integer> function = Integer::parseInt;

		apply(function, "forty two");
	}


	@Deprecated
	@Test
	public void closeAfterApplyingSucceeds()
	{
		int openPort = closeAfterApplying(ServerSocket::new, 0, ServerSocket::getLocalPort);

		assertThat(openPort, is(greaterThan(0)));
	}


	@Deprecated
	@Test
	public void closeAfterAcceptingWhenCreateThrows()
	{
		AtomicBoolean wasCalled = new AtomicBoolean(false);

		try {
			closeAfterAccepting(ServerSocket::new, Integer.MAX_VALUE, c -> wasCalled.set(true));
			fail();
		} catch(IllegalArgumentException e) {}

		assertThat(wasCalled.get(), is(false));
	}


	@Deprecated
	@Test
	public void closeAfterAcceptingWhenCloseThrows()
	{
		AtomicBoolean wasCalled = new AtomicBoolean(false);
		IOException exception = new IOException("24/7, never closes");

		try {
			closeAfterAccepting(new ThrowsAtClosingTime(exception), c -> wasCalled.set(true));
			fail();
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(exception)));
		}

		assertThat(wasCalled.get(), is(true));
	}


	@Deprecated
	@Test
	public void closeAfterApplyingWhenCloseThrows()
	{
		AtomicBoolean wasCalled = new AtomicBoolean(false);
		IOException exception = new IOException("24/7, never closes");

		try {
			closeAfterApplying(ThrowsAtClosingTime::new, exception, c -> {
				wasCalled.set(true);
				return null;
			});
			fail();
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(exception)));
		}

		assertThat(wasCalled.get(), is(true));
	}


	@Deprecated
	@Test
	public void closeAfterApplyingWhenSuccessfulThenCloses()
	{
		ServerSocket socket = createRandomPortServerSocket();
		closeAfterApplying(socket, ServerSocket::getLocalPort);

		assertThat(socket.isClosed(), is(true));
	}


	@Deprecated
	@Test
	public void closeAfterApplyingWhenThrowsThenStillCloses()
	{
		IOException checked = new IOException("whooosh, bang");
		ServerSocket socket = createRandomPortServerSocket();
		try {
			closeAfterApplying(socket, s -> {
				throw checked;
			});
			fail();
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(checked)));
		}

		assertThat(socket.isClosed(), is(true));
	}


	@Deprecated
	@Test
	public void closeAfterAcceptingSucceeds()
	{
		AtomicReference<ObjectOutputStream> ref = new AtomicReference<>();
		closeAfterAccepting(ObjectOutputStream::new, new ByteArrayOutputStream(), ref::set);

		assertThat(ref.get(), is(notNullValue()));
	}


	@Deprecated
	@Test
	public void closeAfterAcceptingClosesWhenConsumerThrows()
	{
		ServerSocket socket = createRandomPortServerSocket();
		try {
			closeAfterAccepting(socket, c -> {
				throw new IOException("nay hungry, nay acceptin' that");
			});
			fail();
		} catch(Exception e) {}

		assertThat(socket.isClosed(), is(true));
	}


	private static ServerSocket createRandomPortServerSocket()
	{
		return apply(ServerSocket::new, 0);
	}


	@Deprecated
	@Test
	public void closeAfterAcceptingClosesWhenConsumerThrowsInterruptAndResetsInterruptedFlag()
	{
		ServerSocket socket = createRandomPortServerSocket();
		try {
			closeAfterAccepting(socket, c -> {
				throw new InterruptedException("'scuse me!");
			});
			fail();
		} catch(Exception e) {}

		assertThat(socket.isClosed(), is(true));
		assertThat(Thread.currentThread().isInterrupted(), is(true));
	}


	@Test
	public void uncheckToIntBiFunctionThrowsNPE()
	{
		ToIntBiFunction<String, String> fn = uncheckToIntBiFunction(ExceptionalTest::cmp);
		try {
			fn.applyAsInt(null, null);
			fail();
		} catch(NullPointerException npe) {}
	}


	private static int cmp(String a, String b)
	{
		return a.compareTo(b);
	}


	@Test
	public void uncheckToIntBiFunctionInvoked()
	{
		ToIntBiFunction<String, String> fn = uncheckToIntBiFunction(ExceptionalTest::cmp);
		int cmp = fn.applyAsInt("a", "a");
		assertThat(cmp, is(0));
	}


	@Test
	public void iterableForEachThrows()
	{
		IOException curveBall = new IOException();
		try {
			Exceptional.forEach(Collections.singleton("bang?"), s -> {
				throw curveBall;
			});
			fail();
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
			fail();
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
