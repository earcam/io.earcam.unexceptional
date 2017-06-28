## Examples

Some examples are available in the [src/example/java][github-unexceptional-examples] directory of the [github project][github-unexceptional], a few sample snippets reproduced below:


### General Examples


#### Convert 

Convert checked Functional Interface instances and Method Handles to unchecked `java.util.function.*` Functional Interfaces.

Given a method that declares a checked exception:

	import java.io.IOException;
	
	class ExceptionalExample {
	
		private static int parse(String text) throws IOException
		{
			try {
				return Integer.parseInt(text);
			} catch(NumberFormatException nfe) {
				throw new IOException(nfe);
			}
		}
	}

Without <span class="color-highlight">Un</span>exceptional, you might write an ugly lambda like so:

	import java.io.IOException;
	import java.io.UncheckedIOException;
	import java.util.function.ToIntFunction;
	
	// ...
	
	public void convertFunctional()
	{
		ToIntFunction<String> a = value -> {
			try {
				return ExceptionalExample.parse(value);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		
		a.applyAsInt("42");
	}

With <span class="color-highlight">Un</span>exceptional, you can just write:

	import java.io.IOException;
	import java.io.UncheckedIOException;
	import java.util.function.ToIntFunction;
	
	// ...
	
	public void convertFunctional()
	{
		ToIntFunction<String> b = Exceptional.uncheckToIntFunction(ExceptionalExample::parse);
		
		a.applyAsInt("42");
	}


#### Invocation

Invoking checked functional types and method handles without wrapping in try-catch.

As an alternative to converting functional types, you can just invoke directly.  

Given the contrived `parse` method from previous example, you can use <span class="color-highlight">Un</span>exceptional to invoke directly:

	import io.earcam.unexceptional.Exceptional;
	
	// ...
	
	public void invokeFunctional()
	{
		int fortyTwo = Exceptional.applyAsInt(ExceptionalExample::parse, "42");
	}	


### Specific Examples

#### Free Server Port Finder

Without <span class="color-highlight">Un</span>exceptional we would write this method (using Java 7's try-with-resource and `java.io.UncheckedIOException` from Java 8) as:

	import java.net.ServerSocket;
	
	// ...
	
	public static int findFreePort()
	{
		try(ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

Using <span class="color-highlight">Un</span>exceptional we can rewrite this in a more terse form as:

	import java.net.ServerSocket;
	
	import static io.earcam.unexceptional.Exceptional.closeAfterApplying;
	
	// ...
	
	public static int findFreePort()
	{
		return closeAfterApplying(ServerSocket::new, 0, ServerSocket::getLocalPort);
	} 


Note; many IO related classes have constructors which declare checked exceptions.



#### De/Serialization

Without <span class="color-highlight">Un</span>exceptional:

	import java.io.ByteArrayInputStream;
	import java.io.ByteArrayOutputStream;
	import java.io.ObjectInputStream;
	import java.io.ObjectOutputStream;
	
	// ...
	
	public static byte[] serialize(Object object)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try(ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
			return baos.toByteArray();
		} catch(IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] serialized, Class<T> type)
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		
		
		try(ObjectInputStream ois = new ObjectInputStream(bais)) {
			return (T) ois.readObject();
		} catch(IOException ioe) {
			throw new UncheckedIOException(ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		}
	}

Granted the above `deserialize(...)` method could be more concise by being less specific 
regarding the exception handling - but that's partly the point of this library.

With <span class="color-highlight">Un</span>exceptional:


	import java.io.ByteArrayInputStream;
	import java.io.ByteArrayOutputStream;
	import java.io.ObjectInputStream;
	import java.io.ObjectOutputStream;
	
	import io.earcam.unexceptional.Exceptional;
	
	// ...
	
	public static byte[] serialize(Object object)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Exceptional.closeAfterAccepting(ObjectOutputStream::new, baos, o -> o.writeObject(object));
		return baos.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] serialized, Class<T> type)
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		return (T) Exceptional.closeAfterApplying(ObjectInputStream::new, bais, ObjectInputStream::readObject);
	}



[github-unexceptional]: https://github.com/earcam/io.earcam.unexceptional/
[github-unexceptional-examples]: https://github.com/earcam/io.earcam.unexceptional/tree/master/src/example/java/io/earcam/unexceptional/example