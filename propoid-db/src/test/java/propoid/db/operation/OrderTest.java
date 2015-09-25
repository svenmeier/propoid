package propoid.db.operation;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import propoid.db.BuildConfig;
import propoid.db.Order;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class OrderTest extends TestCase {

	@Test
	public void testEquals() throws Exception {

		Order order = Order.ascending(new Foo().stringP);

		assertTrue(Order.ascending(new Foo().stringP).equals(order));
		assertFalse(Order.descending(new Foo().stringP).equals(order));
		assertFalse(Order.ascending(new Foo().intP).equals(order));
		assertFalse(Order.descending(new Foo().intP).equals(order));
	}

	@Test
	public void testHashCode() throws Exception {

		Order order = Order.ascending(new Foo().stringP);

		assertTrue(Order.ascending(new Foo().stringP).hashCode() == order
				.hashCode());
	}
}