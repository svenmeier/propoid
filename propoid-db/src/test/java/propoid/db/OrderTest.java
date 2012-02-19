package propoid.db;

import junit.framework.TestCase;
import propoid.core.Property;
import propoid.core.Propoid;

public class OrderTest extends TestCase {

	public void testEquals() throws Exception {

		Order order = Order.ascending(new Foo().bar);

		assertTrue(Order.ascending(new Foo().bar).equals(order));
		assertFalse(Order.descending(new Foo().bar).equals(order));
		assertFalse(Order.ascending(new Foo().baz).equals(order));
		assertFalse(Order.descending(new Foo().baz).equals(order));
	}

	public void testHashCode() throws Exception {

		Order order = Order.ascending(new Foo().bar);

		assertTrue(Order.ascending(new Foo().bar).hashCode() == order
				.hashCode());
	}

	public static class Foo extends Propoid {
		public final Property<String> bar = property();
		public final Property<String> baz = property();
	}
}