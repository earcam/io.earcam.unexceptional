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

import static io.earcam.unexceptional.Closing.autoClosing;
import static io.earcam.unexceptional.Exceptional.unwrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import io.earcam.unexceptional.Closing.AutoClosed;

/**
 * Cases that can't be met as we're assigning method arg to local var in the "with" of try-with-resources:
 * <ul>
 * <li>closesAfterAcceptingThrowsOnCreateResource</li>
 * <li>closesAfterBiAcceptingThrowsOnCreateResource</li>
 * <li>closesAfterApplyingThrowsOnCreateResource</li>
 * <li>closesAfterBiApplyingThrowsOnCreateResource</li>
 * </ul>
 */
public class ClosingTest {

	private final IOException onWrite = new IOException("Two writes don't make a left");
	private final IOException onClose = new IOException("Sorry Dave, we're open 24/7");

	private static class DummyOutputStream extends OutputStream {

		private final IOException thrownOnWrite;
		private final IOException thrownOnClose;
		boolean wasClosed;


		public DummyOutputStream(IOException thrownOnWrite, IOException thrownOnClose)
		{
			this.thrownOnWrite = thrownOnWrite;
			this.thrownOnClose = thrownOnClose;
		}


		@Override
		public void write(int b) throws IOException
		{
			if(thrownOnWrite != null) {
				throw thrownOnWrite;
			}
		}


		@Override
		public void close() throws IOException
		{
			wasClosed = true;
			if(thrownOnClose != null) {
				throw thrownOnClose;
			}
		}
	}

	private static class DodgyConstructorOutputStream extends OutputStream {

		public DodgyConstructorOutputStream(RuntimeException thisRightNow)
		{
			if(thisRightNow != null) {
				throw thisRightNow;
			}
		}


		@Override
		public void write(int b)
		{}

	}


	// Closing.closeAfterAccepting(C, CheckedConsumer<C>)

	@Test
	public void closesAfterAccepting() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, null);

		AtomicReference<OutputStream> ref = new AtomicReference<>();

		Closing.closeAfterAccepting(output, ref::set);

		assertThat(ref.get(), is(equalTo(output)));
		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterAcceptingNull() throws Throwable
	{
		DummyOutputStream output = null;

		AtomicReference<OutputStream> ref = new AtomicReference<>();

		Closing.closeAfterAccepting(output, ref::set);

		assertThat(ref.get(), is(nullValue()));
	}


	@Test
	public void closesAfterAcceptingThrowsOnConsumption() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, null);

		CheckedConsumer<OutputStream> consumer = o -> o.write(1);
		try {
			Closing.closeAfterAccepting(output, consumer);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterAcceptingThrowsOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, onClose);

		CheckedConsumer<OutputStream> consumer = o -> o.write(1);
		try {
			Closing.closeAfterAccepting(output, consumer);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterAcceptingThrowsOnConsumptionAndOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, onClose);

		CheckedConsumer<OutputStream> consumer = o -> o.write(1);
		try {
			Closing.closeAfterAccepting(output, consumer);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
			assertThat(e.getSuppressed(), is(arrayContaining(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	// Closing.closeAfterAccepting(C, U, CheckedBiConsumer<C, U>)

	@Test
	public void closesAfterBiAccepting() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, null);

		AtomicReference<OutputStream> ref = new AtomicReference<>();

		CheckedBiConsumer<OutputStream, Integer> biConsumer = (o, x) -> ref.set(o);

		Closing.closeAfterAccepting(output, 42, biConsumer);

		assertThat(ref.get(), is(equalTo(output)));
		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterBiAcceptingNull() throws Throwable
	{
		DummyOutputStream output = null;

		AtomicReference<OutputStream> ref = new AtomicReference<>();

		CheckedBiConsumer<OutputStream, Integer> biConsumer = (o, x) -> ref.set(o);

		Closing.closeAfterAccepting(output, 42, biConsumer);

		assertThat(ref.get(), is(nullValue()));
	}


	@Test
	public void closesAfterBiAcceptingThrowsOnConsumption() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, null);

		CheckedBiConsumer<OutputStream, Integer> consumer = (o, x) -> o.write(1);
		try {
			Closing.closeAfterAccepting(output, 42, consumer);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterBiAcceptingThrowsOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, onClose);

		CheckedBiConsumer<OutputStream, Integer> consumer = (o, x) -> o.write(1);
		try {
			Closing.closeAfterAccepting(output, 42, consumer);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterBiAcceptingThrowsOnConsumptionAndOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, onClose);

		CheckedBiConsumer<OutputStream, Integer> consumer = (o, x) -> o.write(1);
		try {
			Closing.closeAfterAccepting(output, 42, consumer);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
			assertThat(e.getSuppressed(), is(arrayContaining(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterApplying() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, null);

		DummyOutputStream returned = Closing.closeAfterApplying(output, CheckedFunction.identity());

		assertThat(returned, is(sameInstance(output)));
		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterApplyingNull() throws Throwable
	{
		DummyOutputStream output = null;

		DummyOutputStream returned = Closing.closeAfterApplying(output, CheckedFunction.identity());

		assertThat(returned, is(nullValue()));
	}


	@Test
	public void closesAfterApplyingThrowsOnConsumption() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, null);

		@SuppressWarnings("resource")
		CheckedFunction<OutputStream, Void> function = o -> {
			output.write(1);
			return null;
		};
		try {
			Closing.closeAfterApplying(output, function);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterApplyingThrowsOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, onClose);

		@SuppressWarnings("resource")
		CheckedFunction<OutputStream, Void> function = o -> {
			output.write(1);
			return null;
		};
		try {
			Closing.closeAfterApplying(output, function);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterApplyingThrowsOnConsumptionAndOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, onClose);

		@SuppressWarnings("resource")
		CheckedFunction<OutputStream, Void> function = o -> {
			output.write(1);
			return null;
		};
		try {
			Closing.closeAfterApplying(output, function);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
			assertThat(e.getSuppressed(), is(arrayContaining(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	// Closing.closeAfterBiApplying(C, U, CheckedBiFunction<C, U, R>)

	@Test
	public void closesAfterBiApplying() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, null);

		String ok = "ok";
		CheckedBiFunction<OutputStream, Integer, String> function = (o, n) -> ok;

		String everything = Closing.closeAfterApplying(output, 42, function);

		assertThat(everything, is(ok));
		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterBiApplyingNull() throws Throwable
	{
		DummyOutputStream output = null;

		CheckedBiFunction<OutputStream, Integer, OutputStream> function = (o, n) -> o;

		OutputStream returned = Closing.closeAfterApplying(output, 42, function);

		assertThat(returned, is(nullValue()));
	}


	@Test
	public void closesAfterBiApplyingThrowsOnConsumption() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, null);

		@SuppressWarnings("resource")
		CheckedBiFunction<OutputStream, Void, Void> function = (o, v) -> {
			output.write(1);
			return null;
		};
		try {
			Closing.closeAfterApplying(output, null, function);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterBiApplyingThrowsOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(null, onClose);

		@SuppressWarnings("resource")
		CheckedBiFunction<OutputStream, Void, Void> function = (o, v) -> {
			output.write(1);
			return null;
		};
		try {
			Closing.closeAfterApplying(output, null, function);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	@Test
	public void closesAfterBiApplyingThrowsOnConsumptionAndOnClose() throws Throwable
	{
		DummyOutputStream output = new DummyOutputStream(onWrite, onClose);

		@SuppressWarnings("resource")
		CheckedBiFunction<OutputStream, Void, Void> function = (o, v) -> {
			output.write(1);
			return null;
		};
		try {
			Closing.closeAfterApplying(output, null, function);
		} catch(UncheckedIOException e) {
			assertThat(e.getCause(), is(sameInstance(onWrite)));
			assertThat(e.getSuppressed(), is(arrayContaining(onClose)));
		}

		assertThat(output.wasClosed, is(true));
	}


	// create*

	@Test
	public void closesAfterCreatingThenAccepting() throws Throwable
	{
		AtomicReference<DodgyConstructorOutputStream> ref = new AtomicReference<>();

		Closing.closeAfterAccepting(DodgyConstructorOutputStream::new, null, ref::set);

		assertThat(ref.get(), is(instanceOf(DodgyConstructorOutputStream.class)));
	}


	@Test
	public void closesAfterAcceptingThrowsOnCreate() throws Throwable
	{
		AtomicReference<DodgyConstructorOutputStream> ref = new AtomicReference<>();
		UnsupportedOperationException chuckInConstructor = new UnsupportedOperationException("bang");

		try {
			Closing.closeAfterAccepting(DodgyConstructorOutputStream::new, chuckInConstructor, ref::set);
			fail("should not reach here");
		} catch(UnsupportedOperationException e) {
			assertThat(e, is(sameInstance(chuckInConstructor)));
			assertThat(ref.get(), is(nullValue()));
		}
	}


	@Test
	public void closesAfterCreatingThenBiAccepting() throws Throwable
	{
		AtomicReference<OutputStream> ref = new AtomicReference<>();
		CheckedBiConsumer<OutputStream, Integer> biConsumer = (o, x) -> ref.set(o);

		Closing.closeAfterAccepting(DodgyConstructorOutputStream::new, null, 42, biConsumer);

		assertThat(ref.get(), is(instanceOf(DodgyConstructorOutputStream.class)));
	}


	@Test
	public void closesAfterBiAcceptingThrowsOnCreate() throws Throwable
	{
		AtomicReference<OutputStream> ref = new AtomicReference<>();
		UnsupportedOperationException chuckInConstructor = new UnsupportedOperationException("bang");

		CheckedBiConsumer<OutputStream, Integer> biConsumer = (o, x) -> ref.set(o);

		try {
			Closing.closeAfterAccepting(DodgyConstructorOutputStream::new, chuckInConstructor, 42, biConsumer);
			fail("should not reach here");
		} catch(UnsupportedOperationException e) {
			assertThat(e, is(sameInstance(chuckInConstructor)));
			assertThat(ref.get(), is(nullValue()));
		}
	}


	@Test
	public void closesAfterCreatingThenApplying() throws Throwable
	{
		OutputStream returned = Closing.closeAfterApplying(DodgyConstructorOutputStream::new, null, CheckedFunction.identity());

		assertThat(returned, is(instanceOf(DodgyConstructorOutputStream.class)));
	}


	@Test
	public void closesAfterApplyingThrowsOnCreate() throws Throwable
	{
		AtomicReference<DodgyConstructorOutputStream> ref = new AtomicReference<>();
		UnsupportedOperationException chuckInConstructor = new UnsupportedOperationException("bang");

		try {
			Closing.closeAfterApplying(DodgyConstructorOutputStream::new, chuckInConstructor, CheckedFunction.identity());
			fail("should not reach here");
		} catch(UnsupportedOperationException e) {
			assertThat(e, is(sameInstance(chuckInConstructor)));
			assertThat(ref.get(), is(nullValue()));
		}
	}


	@Test
	public void cannotConstructWithoutObjenesis() throws Exception
	{
		Constructor<Closing> constructor = Closing.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			fail("should not reach here");
		} catch(InvocationTargetException e) {
			assertThat(unwrap(e), is(instanceOf(IllegalStateException.class)));
		}
	}

	private final CheckedTypedConsumer<AtomicBoolean, IOException> closeMethod = i -> i.set(true);
	private final AtomicBoolean unclosable = new AtomicBoolean(false);


	@Test
	public void autoClosingInstanceAvailable()
	{
		try(AutoClosed<AtomicBoolean, IOException> closable = autoClosing(unclosable, closeMethod)) {
			assertThat(closable.get(), is(sameInstance(unclosable)));
		} catch(IOException ioe) {
			fail();
		}
	}


	@Test
	public void autoClosingNotCalledWithinTryBody()
	{
		try(AutoClosed<AtomicBoolean, IOException> closable = autoClosing(unclosable, closeMethod)) {
			assertThat(closable.get().get(), is(false));
		} catch(IOException ioe) {
			fail();
		}
	}


	@Test
	public void autoClosingInvokedImplicitlyByTryWithStatement()
	{
		try(AutoClosed<AtomicBoolean, IOException> closable = autoClosing(unclosable, closeMethod)) {

		} catch(IOException ioe) {
			fail();
		}
		assertThat(unclosable.get(), is(true));
	}
}
