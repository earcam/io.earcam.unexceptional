package io.earcam.unexceptional;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

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
				(CheckedSupplier<List<Integer>>) ArrayList::new,
				List<Integer>::add,
				(CheckedBiConsumer<List<Integer>, List<Integer>>) (left, right) -> {
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
		Stream<Integer> stream = (Stream<Integer>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Stream.class }, nayMock);

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
}
