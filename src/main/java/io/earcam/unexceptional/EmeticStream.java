package io.earcam.unexceptional;

import static io.earcam.unexceptional.Exceptional.*;

import java.io.Serializable;   //NOSONAR SonarQube false positive - putting @SuppressWarnings("squid:UselessImportCheck") on class has no effect, can't put at package level either
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * <p>
 * {@link EmeticStream} provides a functional parallel to {@link Stream}, with methods taking checked equivalents of {@link java.util.function} 
 * </p>
 *  
 * <h1>emetic</h1>
 * <b>/ɪˈmɛtɪk/&nbsp;</b>
 * <p>
 * <i>adjective:</i>
 * </p>
 * <p>
 *  1. (of a substance) causing vomiting.
 * </p>
 * 
 * @param <T> the element type of this stream
 */
@FunctionalInterface
public interface EmeticStream<T> {


	/**
	 * @return the wrapped stream
	 */
	public abstract Stream<T> mapToStream();


	/**
	 * @param function the checked function which, when applied to the {@code value} produces the {@link Stream}
	 * to be returned
	 * @param value argument for the function
	 * @return the {@link Stream} result of applying {@code function} to {@code value}
	 * 
	 * @param <T> the function's argument type
	 * @param <R> the element type of this stream
	 */
	public static <T, R> EmeticStream<R> emesis(CheckedFunction<T, Stream<R>> function, T value)
	{
		return emesis(apply(function, value));
	}
	

	/**
	 * Wrap a stream in order to invoke check functions on it
	 * 
	 * @param stream the underlying {@link Stream}
	 * @return an {@link EmeticStream}
	 * 
 	 * @param <T> the {@code stream}'s element type 
	 */
	@SuppressWarnings("squid:S1905") //SonarQube false positive
	public static <T> EmeticStream<T> emesis(Stream<T> stream)
	{
		return (EmeticStream<T> & Serializable) () -> stream; 
	}


	/**
	 * @return a parallel emetic stream
	 * 
	 * @see Stream#parallel()
	 */
	public default EmeticStream<T> parallel()
	{
		return mapToStream().isParallel() ? this : emesis(mapToStream().parallel());
	}


	/**
	 * @return a sequential emetic stream
	 * 
	 * @see Stream#sequential()
	 */
	public default EmeticStream<T> sequential()
	{
		return !mapToStream().isParallel() ? this : emesis(mapToStream().sequential());
	}
	

	/**
	 * @param predicate a non-interfering and stateless, checked predicate 𝗣 to apply 
	 * to all elements in the stream 𝕊
	 * @return ∀ e ∈ 𝕊: 𝗣(e)
	 * 
	 * @see Stream#allMatch(java.util.function.Predicate)
	 */
	public default boolean allMatch(CheckedPredicate<? super T> predicate)
	{
		return mapToStream().allMatch(uncheckPredicate(predicate));
	}
	
	
	/**
	 * @param predicate a non-interfering and stateless, checked predicate 𝗣 to apply 
	 * to all elements in the stream 𝕊
	 * @return ∃ e ∈ 𝕊: 𝗣(e)
	 * 
	 * @see Stream#anyMatch(java.util.function.Predicate)
	 */
	public default boolean anyMatch(CheckedPredicate<? super T> predicate)
	{
		return mapToStream().anyMatch(uncheckPredicate(predicate));
	}
	
	
	/**
	 * @param collector {@link Collector} performing reduction and supplying result
	 * @return the result 
	 * 
	 * @param <A> mutable accumulator type
	 * @param <R> reduction result type
	 * 
	 * @see Stream#collect(Collector)
	 */
	public default <R, A> R collect(Collector<? super T, A, R> collector)
	{
		return mapToStream().collect(collector);
	}
	
	
	/**
	 * @param supplier creates a new (potentially partial) result container 
	 * @param accumulator associative, non-interfering, stateless function, adding element to result
	 * @param combiner combines the accumulated results
	 * @return the collected result
	 * 
	 * @param <R> result type
	 * 
	 * @see Stream#collect(java.util.function.Supplier, java.util.function.BiConsumer, java.util.function.BiConsumer)
	 */
	public default <R> R collect(CheckedSupplier<R> supplier, CheckedBiConsumer<R, ? super T> accumulator, CheckedBiConsumer<R, R> combiner)
	{
		return mapToStream().collect(uncheckSupplier(supplier), uncheckBiConsumer(accumulator), uncheckBiConsumer(combiner));
	}
	
	
	/**
	 * @param predicate a non-interfering, stateless predicate determining which elements travel down{@code Stream}
	 * @return the new {@link EmeticStream}
	 * 
	 * @see Stream#filter(java.util.function.Predicate)
	 */
	public default EmeticStream<T> filter(CheckedPredicate<? super T> predicate)
	{
		return emesis(mapToStream().filter(uncheckPredicate(predicate)));
	}
	
	
	/**
	 * @param <R> element type of the new stream
	 * 
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code Stream<R>}
	 * @return the new {@link EmeticStream}
	 * 
	 * @see Stream#flatMap(java.util.function.Function)
	 */
	public default <R> EmeticStream<R> flatMap(CheckedFunction<? super T, ? extends Stream<? extends R>> mapper)
	{
		return emesis(mapToStream().flatMap(uncheckFunction(mapper)));
	}
	
	
	/**
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code DoubleStream}
	 * @return the new {@link DoubleStream}
	 * 
	 * @see Stream#flatMapToDouble(java.util.function.Function)
	 */
	public default DoubleStream flatMapToDouble(CheckedFunction<? super T, ? extends DoubleStream> mapper)
	{
		return mapToStream().flatMapToDouble(uncheckFunction(mapper));
	}
	

	/**
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code IntStream}
	 * @return the new {@link IntStream}
	 * 
	 * @see Stream#flatMapToInt(java.util.function.Function)
	 */
	public default IntStream flatMapToInt(CheckedFunction<? super T, ? extends IntStream> mapper)
	{
		return mapToStream().flatMapToInt(uncheckFunction(mapper));
	}


	/**
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code LongStream}
	 * @return the new {@link EmeticStream}
	 * 
	 * @see Stream#flatMapToLong(java.util.function.Function)
	 */
	public default LongStream flatMapToLong(CheckedFunction<? super T, ? extends LongStream> mapper)
	{
		return mapToStream().flatMapToLong(uncheckFunction(mapper));
	}


	/**
	 * @param action a non-interfering action to apply to each element
	 * @see Stream#forEach(java.util.function.Consumer)
	 */
	public default void forEach(CheckedConsumer<? super T> action)
	{
		mapToStream().forEach(uncheckConsumer(action));
	}


	/**
	 * @param action a non-interfering action to apply to each element
	 * @see Stream#forEachOrdered(java.util.function.Consumer)
	 */
	public default void forEachOrdered(CheckedConsumer<? super T> action)
	{
		mapToStream().forEachOrdered(uncheckConsumer(action));
	}

	
	/**
	 * @param <R> the element type of returned {@link Stream}
	 * 
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code <R>}
	 * @return the new {@link EmeticStream}
	 * 
	 * @see Stream#map(java.util.function.Function)
	 */
	public default <R> EmeticStream<R> map(CheckedFunction<? super T, ? extends R> mapper)
	{
		return emesis(mapToStream().map(uncheckFunction(mapper)));
	}
	
	
	/**
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code double}
	 * @return the new {@link EmeticStream}
	 * 
	 * @see Stream#mapToDouble(java.util.function.ToDoubleFunction)
	 */
	public default DoubleStream mapToDouble(CheckedToDoubleFunction<? super T> mapper)
	{
		return mapToStream().mapToDouble(uncheckToDoubleFunction(mapper));
	}
	
	
	/**
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code int}
	 * @return the new {@link EmeticStream}
	 * 
	 * @see Stream#mapToInt(java.util.function.ToIntFunction)
	 */
	public default IntStream mapToInt(CheckedToIntFunction<? super T> mapper)
	{
		return mapToStream().mapToInt(uncheckToIntFunction(mapper));
	}
	
	
	/**
	 * @param mapper a non-interfering, stateless function transforming {@code <T>} to {@code long}
	 * @return the new {@link EmeticStream}
	 * 
	 * @see Stream#mapToLong(java.util.function.ToLongFunction)
	 */
	public default LongStream mapToLong(CheckedToLongFunction<? super T> mapper)
	{
		return mapToStream().mapToLong(uncheckToLongFunction(mapper));
	}

	
	/**
	 * @param comparator a non-interfering, stateless comparator
	 * @return an {@link Optional} as per {@link Stream#max(java.util.Comparator)}
	 * 
	 * @see Stream#max(java.util.Comparator)
	 */
	public default Optional<T> max(CheckedComparator<? super T> comparator)
	{
		return mapToStream().max(uncheckComparator(comparator));
	}

	
	/**
	 * @param comparator a non-interfering, stateless comparator
	 * @return an {@link Optional} as per {@link Stream#max(java.util.Comparator)}

	 * @see Stream#min(java.util.Comparator)
	 */
	public default Optional<T> min(CheckedComparator<? super T> comparator)
	{
		return mapToStream().min(uncheckComparator(comparator));
	}


	/**
	 * @param predicate a non-interfering, stateless, checked predicate 𝗣 to apply 
	 * to all elements in the stream 𝕊
	 * 
	 * @return ∄ e ∈ 𝕊: 𝗣(e)
	 * 
	 * @see Stream#noneMatch(java.util.function.Predicate)
	 */
	public default boolean noneMatch(CheckedPredicate<? super T> predicate)
	{
		return mapToStream().noneMatch(uncheckPredicate(predicate));
	}
	

	/**
	 * @param action non-interfering action applied to elements of the underlying Stream
	 * @return the new EmeticStream
	 * 
	 * @see Stream#peek(java.util.function.Consumer)
	 */
	public default EmeticStream<T> peek(CheckedConsumer<? super T> action)
	{
		return emesis(mapToStream().peek(uncheckConsumer(action)));
	}
	
	
	/**
	 * 
	 * @param accumulator an associative, non-interfering and stateless function
	 * @return the reduction
	 * 
	 * @see Stream#reduce(java.util.function.BinaryOperator)
	 */
	public default Optional<T> reduce(CheckedBinaryOperator<T> accumulator)
	{
		return mapToStream().reduce(uncheckBinaryOperator(accumulator));
	}
	
	
	/**
	 * 
	 * @param identity seed value for accumulator
	 * @param accumulator an associative, non-interfering and stateless function
	 * @return the reduction
	 * 
	 * @see Stream#reduce(Object, java.util.function.BinaryOperator)
	 */
	public default T reduce(T identity, CheckedBinaryOperator<T> accumulator)
	{
		return mapToStream().reduce(identity, uncheckBinaryOperator(accumulator));
	}


	/**
	 * 
	 * @param comparator non-interfering, stateless comparator to sort
	 * @return an emetic stream wrapping the underlying sorted stream
	 * 
	 * @see Stream#sorted(java.util.Comparator)
	 */
	public default EmeticStream<T> sorted(CheckedComparator<? super T> comparator)
	{
		return emesis(mapToStream().sorted(Exceptional.uncheckComparator(comparator)));
	}


	/**
	 * @return count of elements in this stream
	 * 
	 * @see Stream#count()
	 */
	public default long count()
	{
		return mapToStream().count();
	}
}
