package propoid.db.operation;

import propoid.core.Property;
import propoid.core.Propoid;

public class Bar extends Propoid {

	public final Property<String> stringP = property();
	public final Property<Integer> intP = property();

	public Bar() {
		stringP.set("stringP");
		intP.set(0);
	}
}