package propoid.ui.convert;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;
import android.util.Xml.Encoding;

/**
 * Test for {@link EnumConverter}.
 */
public class EnumConverterTest extends TestCase {

	public void testFromProperty() throws Exception {
		EnumConverter<Encoding> converter = new EnumConverter<Encoding>();

		assertEquals("UTF_8",
				converter.fromProperty(new Foo().enumP, Encoding.UTF_8));
	}

	public void testToProperty() throws Exception {
		EnumConverter<Encoding> converter = new EnumConverter<Encoding>();

		assertEquals(Encoding.UTF_8,
				converter.toProperty(new Foo().enumP, "UTF_8"));
	}

	public static class Foo extends Propoid {

		public final Property<Encoding> enumP = property();
	}
}
