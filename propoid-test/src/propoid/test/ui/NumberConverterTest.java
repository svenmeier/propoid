package propoid.test.ui;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;
import propoid.ui.convert.Converter;
import propoid.ui.convert.NumberConverter;

/**
 * Test for {@link NumberConverter}.
 */
public class NumberConverterTest extends TestCase {

	public void testFromProperty() throws Exception {
		assertEquals("0",
				this.<Byte> converter().fromProperty(new Foo().byteP, (byte) 0));
		assertEquals(
				"0",
				this.<Short> converter().fromProperty(new Foo().shortP,
						(short) 0));
		assertEquals(
				"0",
				this.<Integer> converter().fromProperty(new Foo().integerP,
						(int) 0));
		assertEquals("0",
				this.<Long> converter().fromProperty(new Foo().longP, (long) 0));
		assertEquals(
				"0",
				this.<Float> converter().fromProperty(new Foo().floatP,
						(float) 0));
		assertEquals(
				"0",
				this.<Double> converter().fromProperty(new Foo().doubleP,
						(double) 0));

		assertEquals("",
				this.<Double> converter().fromProperty(new Foo().doubleP, null));
	}

	public void testToProperty() throws Exception {
		assertEquals(Byte.valueOf((byte) 0), this.<Byte> converter()
				.toProperty(new Foo().byteP, "0"));
		assertEquals(Short.valueOf((short) 0), this.<Short> converter()
				.toProperty(new Foo().shortP, "0"));
		assertEquals(Integer.valueOf(0),
				this.<Integer> converter().toProperty(new Foo().integerP, "0"));
		assertEquals(Long.valueOf(0l),
				this.<Long> converter().toProperty(new Foo().longP, "0"));
		assertEquals((float) 0,
				this.<Float> converter().toProperty(new Foo().floatP, "0"));
		assertEquals((double) 0,
				this.<Double> converter().toProperty(new Foo().doubleP, "0"));

		assertEquals(null,
				this.<Double> converter().toProperty(new Foo().doubleP, ""));
	}

	@SuppressWarnings("unchecked")
	private <T extends Number> Converter<T> converter() {
		return (Converter<T>) new NumberConverter(0);
	}

	public static class Foo extends Propoid {

		public final Property<Byte> byteP = property();
		public final Property<Short> shortP = property();
		public final Property<Integer> integerP = property();
		public final Property<Long> longP = property();
		public final Property<Float> floatP = property();
		public final Property<Double> doubleP = property();

	}
}
