package propoid.test.ui;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;
import propoid.ui.convert.EnumConverter;
import android.util.Xml.Encoding;

/**
 * Test for {@link EnumConverter}.
 */
public class EnumConverterTest extends TestCase {

	public void testFromProperty() throws Exception {
		EnumConverter<Encoding> converter = new EnumConverter<Encoding>(
				new Foo().enumP);

		assertEquals("UTF_8", converter.toString(Encoding.UTF_8));
	}

	public void testToProperty() throws Exception {
		EnumConverter<Encoding> converter = new EnumConverter<Encoding>(
				new Foo().enumP);

		assertEquals(Encoding.UTF_8, converter.fromString("UTF_8"));
	}

	public static class Foo extends Propoid {

		public final Property<Encoding> enumP = property();
	}
}
