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
 * A mapper for {@link Integer} properties.
 */
public class IntegerMapper implements Mapper<Integer> {

	@Override
	public boolean maps(Property<?> property) {
		return Integer.class == property.type()
				|| Integer.TYPE == property.type();
	}

	public String type(Property<Integer> property, Repository repository) {
		return "INTEGER";
	}

	@Override
	public void bind(Property<Integer> property, Repository repository,
			SQLiteStatement statement, int index) {
		Integer value = property.meta().getInternal(property);
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindLong(index, value);
		}
	}

	@Override
	public void retrieve(Property<Integer> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.meta().setInternal(property, null);
		} else {
			property.meta().setInternal(property, cursor.getInt(index));
		}
	}

	@Override
	public String argument(Property<Integer> newParam, Repository repository,
			Integer value) {
		return value.toString();
	}
}
