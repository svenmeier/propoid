package propoid.db;

import junit.framework.TestCase;

import java.util.List;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.mapping.ByteMapper;
import propoid.db.mapping.BytesMapper;
import propoid.db.mapping.CharacterMapper;
import propoid.db.mapping.ClassMapper;
import propoid.db.mapping.DoubleMapper;
import propoid.db.mapping.EnumMapper;
import propoid.db.mapping.FloatMapper;
import propoid.db.mapping.IntegerMapper;
import propoid.db.mapping.LongMapper;
import propoid.db.mapping.PropoidMapper;
import propoid.db.mapping.PropoidsMapper;
import propoid.db.mapping.ShortMapper;
import propoid.db.mapping.StringMapper;

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
		assertTrue(new ClassMapper().maps(foo.classPraw));
		assertTrue(new PropoidMapper().maps(foo.propoidP));
		assertTrue(new PropoidsMapper().maps(foo.propoidsP));
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
		public final Property<Class> classPraw = property();
		public final Property<Foo> propoidP = property();
		public final Property<List<Foo>> propoidsP = property();
		public final Property<E> enumP = property();
	}

	public static enum E {
		E1, E2
	}
}
