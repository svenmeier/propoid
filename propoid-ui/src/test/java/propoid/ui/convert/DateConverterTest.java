package propoid.ui.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;

/**
 * Test for {@link DateConverter}.
 */
public class DateConverterTest extends TestCase {

	public void testFromProperty() throws Exception {
		DateConverter converter = new DateConverter(new SimpleDateFormat(
				"dd.MM.yyyy"), 0);

		assertEquals("01.01.2000",
				converter.fromProperty(new Foo().enumP, new Date(100, 0, 1)));
	}

	public void testToProperty() throws Exception {
		DateConverter converter = new DateConverter(new SimpleDateFormat(
				"dd.MM.yyyy"), 0);

		assertEquals(new Date(100, 0, 1),
				converter.toProperty(new Foo().enumP, "01.01.2000"));
	}

	public static class Foo extends Propoid {

		public final Property<Date> enumP = property();
	}
}
