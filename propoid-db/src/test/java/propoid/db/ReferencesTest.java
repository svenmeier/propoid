package propoid.db;

import android.os.Parcel;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import propoid.core.Propoid;
import propoid.db.aspect.Row;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ReferencesTest {

	@Test
	public void testFrom() {
		List<Foo> foos = new ArrayList<>();
		foos.add(new Foo());

		try {
			References.from(foos);
			fail();
		} catch(IllegalArgumentException expected) {
		}

		Row.setID(foos.get(0), 1l);

		References<Foo> references = References.from(foos);

		assertEquals(1, references.size());

		Reference<Foo> foo = references.iterator().next();
	}

	@Test
	public void testParceable() {
		Parcel parcel = Parcel.obtain();

		References<Foo> toParcel = new References<>(Foo.class, new long[]{1,2,3});

		toParcel.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);

		References fromParcel = References.CREATOR.createFromParcel(parcel);

		assertEquals(toParcel, fromParcel);
	}

	public static class Foo extends Propoid {

	}
}