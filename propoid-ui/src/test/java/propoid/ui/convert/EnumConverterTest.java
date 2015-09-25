package propoid.ui.convert;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;
import propoid.ui.Foo;
import propoid.ui.convert.EnumConverter;
import android.util.Xml.Encoding;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link EnumConverter}.
 */
public class EnumConverterTest {

	@Test
	public void testFromProperty() throws Exception {
		EnumConverter<Encoding> converter = new EnumConverter<Encoding>(
				new Foo().enumP);

		assertEquals("UTF_8", converter.toString(Encoding.UTF_8));
	}

	@Test
	public void testToProperty() throws Exception {
		EnumConverter<Encoding> converter = new EnumConverter<Encoding>(
				new Foo().enumP);

		assertEquals(Encoding.UTF_8, converter.fromString("UTF_8"));
	}
}
