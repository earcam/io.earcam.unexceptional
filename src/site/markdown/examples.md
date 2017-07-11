## Sample Examples

<ul class="nav nav-list">
	<li class="nav-header">sample</li>
	<li>/ˈsɑːmp(ə)l/&nbsp;&nbsp;<span title="Pronunciation" style="display:inline-block;"><input src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAQAAAC1QeVaAAAAi0lEQVQokWNgQAYyQFzGsIJBnwED8DNcBpK+DM8YfjMUokqxMRxg+A9m8TJsBLLSEFKMDCuBAv/hCncxfGWQhUn2gaVAktkMXkBSHmh0OwNU8D9csoHhO4MikN7BcAGb5H+GYiDdCTQYq2QubkkkY/E6CLtXdiJ7BTMQMnAHXxFm6IICvhwY8AYQLgCw2U9d90B8BAAAAABJRU5ErkJggg==" width="14" height="14" type="image" onclick="pronounce('sample--_gb_1.mp3')" /></span></li>
	<li><i>noun:</i>
		<ol>
			<li>
				a small part or quantity intended to show what the whole is like<br/>
				<i>synonyms:</i> 	representative, illustrative, selected, specimen, test, trial, typifying, typical
			</li>
			<li>a sound or piece of music created by sampling</li>
		</ol>
	</li>
	<li><i>verb:</i>
		<ol>
			<li>
				take a sample or samples of (something) for analysis<br/>
			</li>
			<li>ascertain the momentary value of (an analogue signal) many times a second so as to convert the signal to digital form</li>
		</ol>
	</li>
</ul>

<br/>

<ul class="nav nav-list">
	<li class="nav-header">example</li>
	<li>/ɪɡˈzɑːmp(ə)l,ɛɡˈzɑːmp(ə)l//&nbsp;&nbsp;<span title="Pronunciation" style="display:inline-block;"><input src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAQAAAC1QeVaAAAAi0lEQVQokWNgQAYyQFzGsIJBnwED8DNcBpK+DM8YfjMUokqxMRxg+A9m8TJsBLLSEFKMDCuBAv/hCncxfGWQhUn2gaVAktkMXkBSHmh0OwNU8D9csoHhO4MikN7BcAGb5H+GYiDdCTQYq2QubkkkY/E6CLtXdiJ7BTMQMnAHXxFm6IICvhwY8AYQLgCw2U9d90B8BAAAAABJRU5ErkJggg==" width="14" height="14" type="image" onclick="pronounce('example--_gb_1.mp3')" /></span></li>
	<li><i>noun:</i>
		<ol>
			<li>
				a thing characteristic of its kind or illustrating a general rule<br/>
				<i>synonyms:</i> 	specimen, sample, exemplar, exemplification, instance, case, representative case, typical case, case in point, illustration
			</li>
			<li>
				a person or thing regarded in terms of their fitness to be imitated<br/>
				<i>synonyms:</i> precedent, lead, guide, model, pattern, blueprint, template, paradigm, exemplar, ideal, standard 	
			</li>
		</ol>
	</li>
	<li><i>verb:</i>
		<ol>
			<li>be illustrated or exemplified</li>
		</ol>
	</li>
</ul>

<br/>


Exemplars are available in the [src/example/java][github-unexceptional-examples] directory of the [github project][github-unexceptional], a few sample snippets reproduced below:


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