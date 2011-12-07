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

import java.util.Date;

import propoid.core.Property;
import propoid.db.Repository;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * A mapper for {@link Date} properties.
 */
public class DateMapper implements Mapper<Date> {

	@Override
	public boolean maps(Property<?> property) {
		return Date.class == property.type();
	}

	public String type(Property<Date> property, Repository repository) {
		return "INTEGER";
	}

	@Override
	public void bind(Property<Date> property, Repository repository,
			SQLiteStatement statement, int index) {
		Date value = property.meta().getInternal(property);
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindLong(index, value.getTime());
		}
	}

	@Override
	public void retrieve(Property<Date> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.meta().setInternal(property, null);
		} else {
			property.meta().setInternal(property, new Date(cursor.getLong(index)));
		}
	}

	@Override
	public String argument(Property<Date> newParam, Repository repository,
			Date value) {
		return Long.toString(value.getTime());
	}
}
