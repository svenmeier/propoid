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
 * A mapper for {@link Boolean} properties.
 */
public class BooleanMapper implements Mapper<Boolean> {

	@Override
	public boolean maps(Property<?> property) {
		return Boolean.class == property.type()
				|| Boolean.TYPE == property.type();
	}

	public String type(Property<Boolean> property, Repository repository) {
		return "INTEGER";
	}

	@Override
	public void bind(Property<Boolean> property, Repository repository,
			SQLiteStatement statement, int index) {
		Boolean value = property.meta().get(property);
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindLong(index, value ? 1 : 0);
		}
	}

	@Override
	public void retrieve(Property<Boolean> property, Repository repository,
			Cursor cursor, int index) {

		if (cursor.isNull(index)) {
			property.meta().set(property, null);
		} else {
			property.meta().set(property,
					cursor.getInt(index) == 1 ? true : false);
		}
	}

	@Override
	public String argument(Property<Boolean> newParam, Repository repository,
			Boolean value) {
		return value ? "1" : "0";
	}
}
