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

import java.util.Locale;

import propoid.core.Property;
import propoid.db.Repository;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * A mapper for {@link Locale} properties.
 */
public class LocaleMapper implements Mapper<Locale> {

	@Override
	public boolean maps(Property<?> property) {
		return Locale.class == property.meta().type;
	}

	public String type(Property<Locale> property, Repository repository) {
		return "TEXT";
	}

	@Override
	public void bind(Property<Locale> property, Repository repository,
			SQLiteStatement statement, int index) {
		Locale value = property.getInternal();
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, value.toString());
		}
	}

	@Override
	public void retrieve(Property<Locale> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.setInternal(null);
		} else {
			property.setInternal(new Locale(cursor.getString(index)));
		}
	}

	@Override
	public String argument(Property<Locale> newParam, Repository repository,
			Locale value) {
		return value.toString();
	}
}
