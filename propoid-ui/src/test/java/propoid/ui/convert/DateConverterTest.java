package propoid.ui.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import propoid.ui.convert.DateConverter;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link DateConverter}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = propoid.ui.BuildConfig.class, sdk = 18)
public class DateConverterTest {

	@Test
	public void testFromProperty() throws Exception {
		DateConverter converter = new DateConverter(new SimpleDateFormat(
				"dd.MM.yyyy"), 0);

		assertEquals("01.01.2000", converter.toString(new Date(100, 0, 1)));
		assertEquals("", converter.toString(null));
	}

	@Test
	public void testToProperty() throws Exception {
		DateConverter converter = new DateConverter(new SimpleDateFormat(
				"dd.MM.yyyy"), 0);

		assertEquals(new Date(100, 0, 1), converter.fromString("01.01.2000"));
		assertEquals(null, converter.fromString(""));
	}
}
