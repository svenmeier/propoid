package propoid.test;

import java.util.Date;
import java.util.Locale;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.mapping.LocationMapper;
import android.location.Location;
import android.util.Xml.Encoding;

public class Foo extends Propoid {

	public final Property<Boolean> booleanP = property();
	public final Property<Byte> byteP = property();
	public final Property<Short> shortP = property();
	public final Property<Integer> intP = property();
	public final Property<Long> longP = property();
	public final Property<Float> floatP = property();
	public final Property<Double> doubleP = property();
	public final Property<Character> characterP = property();
	public final Property<String> stringP = property();
	public final Property<byte[]> bytesP = property();
	public final Property<Class<?>> classP = property();
	public final Property<Locale> localeP = property();
	public final Property<Date> dateP = property();
	public final Property<Location> locationP = property();
	public final Property<Bar> barP = property();
	public final Property<Encoding> enumP = property();

	public Foo() {
		booleanP.set(true);
		byteP.set(Byte.MAX_VALUE);
		shortP.set(Short.MAX_VALUE);
		intP.set(Integer.MAX_VALUE);
		longP.set(Long.MAX_VALUE);
		floatP.set(Float.MAX_VALUE);
		doubleP.set(Double.MAX_VALUE);
		characterP.set('c');
		stringP.set("string");
		bytesP.set(new byte[] { 0x11, 0x12, 0x13, 0x14, 0x15 });
		classP.set(java.lang.String.class);
		localeP.set(Locale.getDefault());
		dateP.set(new Date());
		locationP.set(LocationMapper.fromString("0.375256 0.883026", ' '));
		enumP.set(Encoding.UTF_8);
	}
}