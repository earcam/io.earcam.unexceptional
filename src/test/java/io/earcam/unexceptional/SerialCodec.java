package io.earcam.unexceptional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

final class SerialCodec {

	private SerialCodec() {}
	
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
		// using type.cast fails for lambdas:    return type.cast(Exceptional.closing(ObjectInputStream::new, bais, ObjectInputStream::readObject));
		return (T) Exceptional.closeAfterApplying(ObjectInputStream::new, bais, ObjectInputStream::readObject);
	}


	public static boolean isSerializable(Object instance)
	{
		return deserialize(serialize(instance), instance.getClass()) != null;
	}


	public static Matcher<Object> serializable()
	{
		return new TypeSafeMatcher<Object>() {

			@Override
			public void describeTo(Description description)
			{
				description.appendText(" serializable");
			}

			@Override
			protected boolean matchesSafely(Object item)
			{
				if(item == null) {
					return false;
				}
				return isSerializable(item);
			}
		};
	}
}
