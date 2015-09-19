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
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * A mapper for {@link String} properties.
 */
public class StringMapper implements Mapper<String> {

	@Override
	public boolean maps(Property<?> property) {
		return String.class == property.meta().type;
	}

	public String type(Property<String> property, Repository repository) {
		return "TEXT";
	}

	@Override
	public void bind(Property<String> property, Repository repository,
			SQLiteStatement statement, int index) {
		String value = property.getInternal();
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, value);
		}
	}

	@Override
	public void retrieve(Property<String> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.setInternal(null);
		} else {
			property.setInternal(cursor.getString(index));
		}
	}

	@Override
	public String argument(Property<String> newParam, Repository repository,
			String value) {
		return value;
	}
}
