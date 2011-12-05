package propoid.core;

import java.util.Iterator;

import junit.framework.TestCase;

public class PropoidTest extends TestCase {

	public void testSetGet() throws Exception {
		Foo foo = new Foo();

		Iterator<Property<?>> properties = foo.properties().iterator();
		assertTrue(properties.hasNext());
		assertEquals(foo.bar, properties.next());
		assertEquals("bar", foo.bar.name());
		assertEquals(String.class, foo.bar.type());
		assertFalse(properties.hasNext());

		foo.bar.set("BAR");
		assertEquals("BAR", foo.bar.get());
	}

	public void testPropertyEquals() throws Exception {
		Foo foo1 = new Foo();
		Foo foo2 = new Foo();
		ExtendedFoo extendedFoo1 = new ExtendedFoo();

		assertTrue(foo1.bar.equals(foo1.bar));
		assertTrue(foo1.bar.equals(foo2.bar));
		assertTrue(foo1.bar.equals(extendedFoo1.bar));
		assertFalse(foo1.bar.equals(extendedFoo1.baz));

		assertTrue(foo2.bar.equals(foo2.bar));
		assertTrue(foo2.bar.equals(foo1.bar));
		assertTrue(foo2.bar.equals(extendedFoo1.bar));
		assertFalse(foo2.bar.equals(extendedFoo1.baz));

		assertTrue(extendedFoo1.bar.equals(foo1.bar));
		assertTrue(extendedFoo1.bar.equals(foo2.bar));
		assertTrue(extendedFoo1.bar.equals(extendedFoo1.bar));
		assertFalse(extendedFoo1.bar.equals(extendedFoo1.baz));

		assertFalse(extendedFoo1.baz.equals(foo1.bar));
		assertFalse(extendedFoo1.baz.equals(foo2.bar));
		assertFalse(extendedFoo1.baz.equals(extendedFoo1.bar));
		assertTrue(extendedFoo1.baz.equals(extendedFoo1.baz));
	}

	public void testInheritance() throws Exception {
		ExtendedFoo foo = new ExtendedFoo();

		Iterator<Property<?>> properties = foo.properties().iterator();
		assertTrue(properties.hasNext());
		assertSame(foo.baz, properties.next());
		assertEquals("bar", foo.bar.name());
		assertEquals(String.class, foo.bar.type());
		assertTrue(properties.hasNext());
		assertSame(foo.bar, properties.next());
		assertEquals("baz", foo.baz.name());
		assertEquals(Integer.class, foo.baz.type());
		assertFalse(properties.hasNext());
	}

	public void testAspects() throws Exception {
		Foo foo = new Foo();

		TestAspect aspect = new TestAspect(foo);

		aspect.reset();
		foo.bar.set("BAR");
		assertTrue(aspect.setCalled);
		foo.bar.get();
		assertTrue(aspect.getCalled);

		Iterator<Aspect> aspects = foo.aspects().iterator();
		assertTrue(aspects.hasNext());
		assertEquals(aspect, aspects.next());
		aspects.remove();
		assertTrue(aspects.hasNext());
		aspects.next();
		assertFalse(aspects.hasNext());

		aspect.reset();
		foo.bar.set("BAR");
		assertFalse(aspect.setCalled);
		foo.bar.get();
		assertFalse(aspect.getCalled);
	}

	public static class Foo extends Propoid {

		public final Property<String> bar = property();
	}

	public static class ExtendedFoo extends Foo {

		public final Property<Integer> baz = property();
	}

	public static class TestAspect extends AbstractAspect {

		public boolean getCalled;

		public boolean setCalled;

		protected TestAspect(Propoid propoid) {
			super(propoid);
		}

		public void reset() {
			getCalled = false;
			setCalled = false;
		}

		public <T> T onSet(Property<T> property, T value) {
			setCalled = true;

			return super.onSet(property, value);
		}

		public <T> T onGet(Property<T> property, T value) {
			getCalled = true;

			return super.onGet(property, value);
		}
	}
}
