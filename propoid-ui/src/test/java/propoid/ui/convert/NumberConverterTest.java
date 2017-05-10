package propoid.ui.convert;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.ui.Foo;
import propoid.ui.convert.Converter;
import propoid.ui.convert.NumberConverter;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link NumberConverter}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = propoid.ui.BuildConfig.class)
public class NumberConverterTest {

	@Test
	public void testFromProperty() throws Exception {
		assertEquals("0", converter(new Foo().byteP).toString((byte) 0));
		assertEquals("0", converter(new Foo().shortP).toString((short) 0));
		assertEquals("0", converter(new Foo().intP).toString((int) 0));
		assertEquals("0", converter(new Foo().longP).toString((long) 0));
		assertEquals("0", converter(new Foo().floatP).toString((float) 0));
		assertEquals("0", converter(new Foo().doubleP).toString((double) 0));

		assertEquals("", converter(new Foo().doubleP).toString(null));
	}

	public void testToProperty() throws Exception {
		assertEquals(Byte.valueOf((byte) 0), converter(new Foo().byteP)
				.fromString("0"));
		assertEquals(Short.valueOf((short) 0), converter(new Foo().shortP)
				.fromString("0"));
		assertEquals(Integer.valueOf(0), converter(new Foo().intP)
				.fromString("0"));
		assertEquals(Long.valueOf(0l),
				converter(new Foo().longP).fromString("0"));
		assertEquals((Object)0f, converter(new Foo().floatP).fromString("0"));
		assertEquals((Object)0d, converter(new Foo().doubleP).fromString("0"));

		assertEquals(null, converter(new Foo().doubleP).fromString(""));
	}

	private <N extends Number> Converter<N> converter(Property<N> property) {
		return new NumberConverter<N>(property, 0);
	}
}
