/*
 * Copyright 2011 Sven Meier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package propoid.db.mapping;

import propoid.core.Property;
import propoid.db.Repository;
import propoid.db.SQL;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;

/**
 * A mapper for {@link Location} properties.
 */
public class LocationMapper implements Mapper<Location> {

	private int format;

	private char separator;

	/**
	 * Conversion with {@link Location#FORMAT_DEGREES} and space separator.
	 */
	public LocationMapper() {
		this(Location.FORMAT_DEGREES, ' ');
	}

	/**
	 * @see Location#FORMAT_DEGREES
	 * @see Location#FORMAT_MINUTES
	 * @see Location#FORMAT_SECONDS
	 */
	public LocationMapper(int format, char separator) {
		this.format = format;
		this.separator = separator;
	}

	@Override
	public boolean maps(Property<?> property) {
		return Location.class == property.meta().type;
	}

	public String type(Property<Location> property, Repository repository) {
		return "TEXT";
	}

	@Override
	public void bind(Property<Location> property, Repository repository,
			SQLiteStatement statement, int index) {
		Location value = property.getInternal();
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, toString(value, format, separator));
		}
	}

	@Override
	public void retrieve(Property<Location> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.setInternal(null);
		} else {
			property.setInternal(fromString(cursor.getString(index), separator));
		}
	}

	@Override
	public String argument(Property<Location> newParam, Repository repository,
			Location value) {
		return toString(value, format, separator);
	}

	public static String toString(Location location, int format, char separator) {
		SQL string = new SQL();

		string.raw(Location.convert(location.getLatitude(), format).replace(
				',', '.'));
		string.raw(Character.toString(separator));
		string.raw(Location.convert(location.getLongitude(), format).replace(
				',', '.'));

		return string.toString();
	}

	public static Location fromString(String string, char separator) {
		Location location = new Location((String) null);

		int index = string.indexOf(separator);

		location.setLatitude(Location.convert(string.substring(0, index)));
		location.setLongitude(Location.convert(string.substring(index + 1)));

		return location;
	}
}
