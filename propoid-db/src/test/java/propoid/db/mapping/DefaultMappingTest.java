package propoid.db.mapping;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;

public class DefaultMappingTest extends TestCase {

	public void test() throws Exception {
		Foo foo = new Foo();

		assertTrue(new StringMapper().maps(foo.stringP));
		assertTrue(new CharacterMapper().maps(foo.characterP));
		assertTrue(new ByteMapper().maps(foo.byteP));
		assertTrue(new ShortMapper().maps(foo.shortP));
		assertTrue(new IntegerMapper().maps(foo.intP));
		assertTrue(new LongMapper().maps(foo.longP));
		assertTrue(new FloatMapper().maps(foo.floatP));
		assertTrue(new DoubleMapper().maps(foo.doubleP));
		assertTrue(new BytesMapper().maps(foo.bytesP));
		assertTrue(new ClassMapper().maps(foo.classP));
		assertTrue(new PropoidMapper().maps(foo.propoidP));
		assertTrue(new EnumMapper().maps(foo.enumP));
	}

	public static class Foo extends Propoid {
		public final Property<String> stringP = property();
		public final Property<Character> characterP = property();
		public final Property<byte[]> bytesP = property();
		public final Property<Short> shortP = property();
		public final Property<Integer> intP = property();
		public final Property<Long> longP = property();
		public final Property<Float> floatP = property();
		public final Property<Double> doubleP = property();
		public final Property<Byte> byteP = property();
		public final Property<Class<?>> classP = property();
		public final Property<Foo> propoidP = property();
		public final Property<E> enumP = property();
	}

	public static enum E {
		E1, E2
	}
}
