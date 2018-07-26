/*-
 * #%L
 * io.earcam.unexceptiona1l
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

import static io.earcam.unexceptional.EmeticStream.emesis;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class EmeticStreamTest {

	@Test
	public void allMatch()
	{
		boolean matched = EmeticStream.emesis(zeroToNine()).allMatch(i -> i < 10);

		assertThat(matched, is(true));
	}


	private static Stream<Integer> zeroToNine()
	{
		return IntStream.range(0, 10).boxed();
	}


	@Test
	public void anyMatch()
	{
		boolean matched = EmeticStream.emesis(zeroToNine()).anyMatch(i -> i == 10);

		assertThat(matched, is(false));
	}


	@Test
	public void noneMatch()
	{
		boolean matched = EmeticStream.emesis(zeroToNine()).noneMatch(i -> i > 1_000);

		assertThat(matched, is(true));
	}


	@Test
	public void collect()
	{
		List<Integer> collected = EmeticStream.emesis(zeroToNine()).collect(toList());

		assertThat(collected, contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
	}


	@Test
	public void collects()
	{
		List<Integer> collected = EmeticStream.emesis(zeroToNine()).collect(
				(CheckedSupplier<List<Integer>, ?>) ArrayList::new,
				List<Integer>::add,
				(CheckedBiConsumer<List<Integer>, List<Integer>, ?>) (left, right) -> {
					left.addAll(right);
				});

		assertThat(collected, contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
	}


	@Test
	public void filter()
	{
		List<Integer> collected = EmeticStream.emesis(zeroToNine()).filter(i -> i % 2 == 0).collect(toList());

		assertThat(collected, contains(0, 2, 4, 6, 8));
	}


	@Test
	public void map()
	{
		List<String> collected = EmeticStream.emesis(zeroToNine()).map(Object::toString).collect(toList());

		assertThat(collected, contains("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
	}


	@Test
	public void mapToInt()
	{
		List<Integer> collected = EmeticStream.emesis(oneToFive()).mapToInt(i -> i).boxed().collect(toList());

		assertThat(collected, contains(1, 2, 3, 4, 5));
	}


	@Test
	public void mapToLong()
	{
		List<Long> collected = EmeticStream.emesis(oneToFive()).mapToLong(i -> i + 1000L).boxed().collect(toList());

		assertThat(collected, contains(1001L, 1002L, 1003L, 1004L, 1005L));
	}


	@Test
	public void mapToDouble()
	{
		List<Double> collected = EmeticStream.emesis(oneToFive()).mapToDouble(i -> i + 0.1D).boxed().collect(toList());

		assertThat(collected, contains(1.1D, 2.1D, 3.1D, 4.1D, 5.1D));
	}


	private static Stream<Integer> oneToFive()
	{
		return IntStream.range(1, 6).boxed();
	}


	@Test
	public void max()
	{
		Optional<Integer> nine = EmeticStream.emesis(zeroToNine()).max(Integer::compareTo);

		assertThat(nine.get(), is(9));
	}


	@Test
	public void min()
	{
		Optional<Integer> zero = EmeticStream.emesis(zeroToNine()).min(Integer::compareTo);

		assertThat(zero.get(), is(0));
	}


	@Test
	public void reduce()
	{
		Optional<Integer> fortyFive = EmeticStream.emesis(zeroToNine()).reduce((a, b) -> a + b);

		assertThat(fortyFive.get(), is(45));
	}


	@Test
	public void reduceWithIdentity()
	{
		Integer fortyFive = EmeticStream.emesis(zeroToNine()).reduce((Integer) 0, (a, b) -> a + b);

		assertThat(fortyFive, is(45));
	}


	@Test
	public void sort()
	{
		List<Integer> sorted = EmeticStream.emesis(zeroToNine()).map(i -> -i).sorted(Integer::compareTo).collect(toList());

		assertThat(sorted, contains(-9, -8, -7, -6, -5, -4, -3, -2, -1, 0));
	}


	@Test
	public void peek()
	{
		List<Integer> peeked = new ArrayList<Integer>();

		List<Integer> collected = EmeticStream.emesis(zeroToNine()).sequential().peek(peeked::add).collect(toList());

		assertThat(peeked, is(equalTo(collected)));
		assertThat(collected, is(equalTo(zeroToNine().collect(toList()))));
	}


	@Test
	public void toParallel()
	{
		Stream<Integer> stream = EmeticStream.emesis(zeroToNine().sequential()).parallel().mapToStream();

		assertThat(stream.isParallel(), is(true));
	}


	@Test
	public void givenUnderlyingAlreadyParallelThenReturnsSelf()
	{
		EmeticStream<Integer> emetic = EmeticStream.emesis(zeroToNine().parallel());

		assertThat(emetic.parallel(), is(sameInstance(emetic)));
	}


	@Test
	public void toSequential()
	{
		Stream<Integer> stream = EmeticStream.emesis(zeroToNine().parallel()).sequential().mapToStream();

		assertThat(stream.isParallel(), is(false));
	}


	@Test
	public void givenUnderlyingAlreadySequentialThenReturnsSelf()
	{
		EmeticStream<Integer> emetic = EmeticStream.emesis(zeroToNine().sequential());

		assertThat(emetic.sequential(), is(sameInstance(emetic)));
	}


	@Test
	public void forEach()
	{
		List<Integer> consumed = new ArrayList<>();

		EmeticStream.emesis(zeroToNine()).forEach(consumed::add);

		assertThat(consumed, is(equalTo(zeroToNine().collect(toList()))));
	}


	@Test
	public void forEachOrdered()
	{
		NayMock nayMock = new NayMock();
		List<Integer> consumed = new ArrayList<>();

		@SuppressWarnings("unchecked")
		Stream<Integer> stream = NayMock.proxy(nayMock, Stream.class);

		EmeticStream.emesis(stream).forEachOrdered(consumed::add);

		assertThat(nayMock.invocations.get(0).name, is(equalTo("forEachOrdered")));
	}


	@Test
	public void flatMap()
	{
		EmeticStream<List<Integer>> stream = EmeticStream
				.emesis(Stream.of(zeroToNine().collect(toList()), zeroToNine().collect(toList()), zeroToNine().collect(toList())));

		assertThat(stream.flatMap(List::stream).count(), is(30L));
	}


	@Test
	public void flatMapToInt()
	{
		EmeticStream<List<Integer>> stream = EmeticStream
				.emesis(Stream.of(zeroToNine().collect(toList()), zeroToNine().collect(toList()), zeroToNine().collect(toList())));

		assertThat(stream.flatMapToInt(s -> s.stream().mapToInt(Integer::intValue)).count(), is(30L));
	}


	@Test
	public void flatMapToLong()
	{
		EmeticStream<List<Integer>> stream = EmeticStream
				.emesis(Stream.of(zeroToNine().collect(toList()), zeroToNine().collect(toList()), zeroToNine().collect(toList())));

		assertThat(stream.flatMapToLong(s -> s.stream().mapToLong(Integer::longValue)).count(), is(30L));
	}


	@Test
	public void flatMapToDouble()
	{
		EmeticStream<List<Integer>> stream = EmeticStream
				.emesis(Stream.of(zeroToNine().collect(toList()), zeroToNine().collect(toList()), zeroToNine().collect(toList())));

		assertThat(stream.flatMapToDouble(s -> s.stream().mapToDouble(Integer::doubleValue)).count(), is(30L));
	}


	@Test
	public void unordered()
	{
		NayMock nayMock = new NayMock();

		@SuppressWarnings("unchecked")
		Stream<Integer> stream = NayMock.proxy(nayMock, Stream.class);

		EmeticStream<Integer> unordered = EmeticStream.emesis(stream).unordered();

		assertThat(nayMock.invocations.get(0).name, is(equalTo("unordered")));
		assertThat(unordered, is(not(nullValue())));
	}


	@Test
	public void iterator()
	{
		NayMock nayMock = new NayMock();

		@SuppressWarnings("unchecked")
		Stream<Integer> stream = NayMock.proxy(nayMock, Stream.class);

		EmeticStream.emesis(stream).iterator();

		assertThat(nayMock.invocations.get(0).name, is(equalTo("iterator")));
	}


	@Test
	public void spliterator()
	{
		NayMock nayMock = new NayMock();

		@SuppressWarnings("unchecked")
		Stream<Integer> stream = NayMock.proxy(nayMock, Stream.class);

		EmeticStream.emesis(stream).spliterator();

		assertThat(nayMock.invocations.get(0).name, is(equalTo("spliterator")));
	}


	@Test
	public void isParallel()
	{
		NayMock nayMock = NayMock.stub(true);

		@SuppressWarnings("unchecked")
		Stream<Integer> stream = NayMock.proxy(nayMock, Stream.class);

		boolean parallel = EmeticStream.emesis(stream).isParallel();

		assertThat(nayMock.invocations.get(0).name, is(equalTo("isParallel")));
		assertThat(parallel, is(true));
	}


	@Test
	public void onClose()
	{
		NayMock nayMock = new NayMock();

		@SuppressWarnings("unchecked")
		Stream<Integer> stream = NayMock.proxy(nayMock, Stream.class);

		Runnable closeHandler = new Runnable() {
			public void run()
			{};
		};
		EmeticStream<Integer> stream2 = EmeticStream.emesis(stream).onClose(closeHandler);

		assertThat(nayMock.invocations.get(0).name, is(equalTo("onClose")));
		assertThat(nayMock.invocations.get(0).args, is(arrayContaining(closeHandler)));
		assertThat(stream2, is(not(nullValue())));
	}


	@Test
	public void close()
	{
		NayMock nayMock = new NayMock();

		@SuppressWarnings("unchecked")
		Stream<Integer> stream = NayMock.proxy(nayMock, Stream.class);

		EmeticStream.emesis(stream).close();

		assertThat(nayMock.invocations.get(0).name, is(equalTo("close")));
	}


	@Test
	public void streamFromASourceThatThrows()
	{
		Path baseDir = Paths.get(".", "target", "test-classes");

		List<Path> paths = emesis(Files::list, baseDir)
				.collect(toList());

		assertThat(paths, is(not(empty())));
	}
}
