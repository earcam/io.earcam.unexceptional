# About

## Building 

This project uses [maven-toolchains-plugin][maven-toolchains-plugin], so you'll need to [setup toolchains][maven-toolchains-plugin-setup].  
Examples for various OS/architectures can be found [here][maven-central-earcam-toolchain] 

With toolchains configured, run `mvn clean install`.

When modifying the code beware/be-aware the build will fail if Maven POMs, Java source or Javascript source aren't formatted according to conventions (Apache 
Maven's standards for POMs, my own undocumented formatting for source).  To auto-format the lot, simply run `mvn -P '!strict,tidy'`.

To run PiTest use `mvn -P analyse`

To run against SonarQube use `mvn -P analyse,sonar`


## Roadmap

General direction without target dates.

### What's missing?

At first glance, compared to `java.util.function` quite a bit...  

... but only functional types handling primitive types - so, thanks to auto-boxing, it's not necessary to implement them (other than for performance).

As such, checked equivalents of the following will be added _on demand_ in future versions:

* BooleanSupplier
* DoubleConsumer
* DoubleFunction
* DoublePredicate
* DoubleSupplier
* DoubleToIntFunction
* DoubleToLongFunction
* DoubleUnaryOperator
* IntBinaryOperator
* IntConsumer
* IntFunction
* IntPredicate
* IntSupplier
* IntToDoubleFunction
* IntToLongFunction
* IntUnaryOperator
* LongBinaryOperator
* LongConsumer
* LongFunction
* LongPredicate
* LongSupplier
* LongToDoubleFunction
* LongToIntFunction
* LongUnaryOperator
* ObjDoubleConsumer
* ObjIntConsumer
* ObjLongConsumer
* ToDoubleBiFunction
* ToDoubleFunction
* ToIntFunction
* ToLongBiFunction
* ToLongFunction
* UnaryOperator


### Features

It would be great to have `EmeticStream` optionally take the equivalent of `java.lang.Thread.UncaughtExceptionHandler` - this could decide
how to handle per-element exceptions.  A default re-throwing implementation could be provided, along with `Collector` implementations such as `toList()` and `summarise()` (similar to `java.util.IntSummaryStatistics`).

Hopefully future versions of Java will allow primitives in generics, so checked parallels of `java.util.stream.IntStream` et al. won't be necessary. 



## Implementation Notes

### Design

Looking inside `Exceptional` you'll see a lot of _duplication_ - necessarily the try-catch blocks that callers no longer have to write.

An earlier version of this code, had all public methods marshal and delegate to one of two methods taking either a checked runnable or callable.  
While this was [DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself), in practice it lead to convoluted lambda-laced stacktraces which were unsavoury, 
so I [KISS](https://en.wikipedia.org/wiki/KISS_principle)ed it goodbye and unrolled the abstraction - likely Dijkstra would understand, if not approve.


### Static Code Analysis 

#### Sonar

Used Sonar 6.0 during development, the issues raised are either:

* Regarding methods declaring `throws Throwable` and `catch` statements catching `Throwable`.  These have just been marked with only `//NOSONAR` in the source, as ordinarily this is a code smell.
* Bugs in Sonar (or the wide collection a libraries it leverages).  These have been marked with `//NOSONAR and some explanation`.

At time of writing I've switched to SonarQube 6.4, and now have 26 more false positives - both tiring of adding `//NOSONAR blah blah` and trying to be a good <abbr title="Open-Source Software">OSS</abbr> citizen - created a SonarQube JIRA account,
only to find you [cannot create SonarQube issues](http://sonarqube-archive.15.x6.nabble.com/How-to-create-Jira-issues-td5034356.html) only report via mailing list (fair enough, very widely used project must 
get a lot of spam issues).

I'll update this section with link to mailing list archive at a later date.

#### Mutation Testing

At time of writing, there are zero survived [PiTest](http://pitest.org) mutations.  A few additional tests were written, with the sole purpose of killing mutations deemed benign. 


### Compiler Warnings

#### AutoClose

Javac (v8 and v9) generates x2 instances of an odd warning for methods handling `AutoCloseable`, first glance it's just an overzealous compiler warning (bug) [JDK-8155591](https://bugs.openjdk.java.net/browse/JDK-8155591).

The [JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html#close--) for `AutoCloseable.close()` states implementors should not throw `InterruptedException`. 
IMO javac should generate a warning/error when compiling an implementation of AutoCloseable.close() that throws InterruptedException, not badger consumers.

	[WARNING] ... Closing.java ... auto-closeable resource C has a member method close() that could throw InterruptedException

Further annoyance as the compiler does no analysis for this warning - so even if you explicitly catch `InterruptedException` and add `Thread.currentThread().interrupt()` your build log will still be polluted with this noddy warning.




[maven-toolchains-plugin]: http://maven.apache.org/plugins/maven-toolchains-plugin/
[maven-toolchains-plugin-setup]: https://maven.apache.org/guides/mini/guide-using-toolchains.html
[maven-central-earcam-toolchain]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22io.earcam.maven.toolchain%22