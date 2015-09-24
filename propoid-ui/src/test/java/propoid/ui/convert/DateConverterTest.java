package propoid.ui.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import propoid.ui.convert.DateConverter;

/**
 * Test for {@link DateConverter}.
 */
public class DateConverterTest extends TestCase {

	public void testFromProperty() throws Exception {
		DateConverter converter = new DateConverter(new SimpleDateFormat(
				"dd.MM.yyyy"), 0);

		assertEquals("01.01.2000", converter.toString(new Date(100, 0, 1)));
		assertEquals("", converter.toString(null));
	}

	public void testToProperty() throws Exception {
		DateConverter converter = new DateConverter(new SimpleDateFormat(
				"dd.MM.yyyy"), 0);

		assertEquals(new Date(100, 0, 1), converter.fromString("01.01.2000"));
		assertEquals(null, converter.fromString(""));
	}
}
