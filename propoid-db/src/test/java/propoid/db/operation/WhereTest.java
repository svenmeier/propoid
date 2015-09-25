package propoid.db.operation;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.BuildConfig;
import propoid.db.Where;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class WhereTest {

	@Test
	public void testWhere() throws Exception {
		Foo foo = new Foo();

		Where.greaterEqual(foo.stringP, "A");
	}
}
