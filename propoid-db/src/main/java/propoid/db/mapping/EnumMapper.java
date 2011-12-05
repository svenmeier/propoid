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
 * A mapper for {@link Enum} properties.
 */
@SuppressWarnings("rawtypes")
public class EnumMapper implements Mapper<Enum> {

	@Override
	public boolean maps(Property<?> property) {
		return property.type() instanceof Class
				&& Enum.class.isAssignableFrom((Class<?>) property.type());
	}

	public String type(Property<Enum> property, Repository repository) {
		return "TEXT";
	}

	@Override
	public void bind(Property<Enum> property, Repository repository,
			SQLiteStatement statement, int index) {
		Enum value = property.meta().get(property);
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, value.name());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void retrieve(Property<Enum> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.meta().set(property, null);
		} else {
			property.meta().set(
					property,
					Enum.valueOf((Class) property.type(),
							cursor.getString(index)));
		}
	}

	@Override
	public String argument(Property<Enum> newParam, Repository repository,
			Enum value) {
		return value.name();
	}
}
