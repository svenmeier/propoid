package propoid.db.operation;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import propoid.db.BuildConfig;
import propoid.db.Order;
import propoid.db.SQL;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class OrderTest extends TestCase {

	@Test
	public void random() throws Exception {

		Order order = Order.random();

		Operation.Aliaser aliaser = new Operation.Aliaser();

		assertEquals(new SQL("random()"), order.toOrderBy(aliaser));
	}

	@Test
	public void testAscendingProperty() throws Exception {

		Foo foo = new Foo();
		Order order = Order.ascending(foo.stringP);

		Operation.Aliaser aliaser = new Operation.Aliaser();
		String alias = aliaser.alias(foo);

		assertEquals(new SQL("a.[stringP] COLLATE NOCASE asc"), order.toOrderBy(aliaser));
	}

	@Test
	public void testDescendingProperty() throws Exception {

		Foo foo = new Foo();
		Order order = Order.descending(foo.stringP);

		Operation.Aliaser aliaser = new Operation.Aliaser();
		String alias = aliaser.alias(foo);

		assertEquals(new SQL("a.[stringP] COLLATE NOCASE desc"), order.toOrderBy(aliaser));
	}

	@Test
	public void testAscendingByInsert() throws Exception {

		Order order = Order.ascendingByInsert();

		Operation.Aliaser aliaser = new Operation.Aliaser();

		assertEquals(new SQL("_id asc"), order.toOrderBy(aliaser));
	}

	@Test
	public void testDescendingByInsert() throws Exception {

		Order order = Order.descendingByInsert();

		Operation.Aliaser aliaser = new Operation.Aliaser();

		assertEquals(new SQL("_id desc"), order.toOrderBy(aliaser));
	}

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