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
import propoid.db.RepositoryException;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * A mapper for {@link Character} properties.
 */
public class CharacterMapper implements Mapper<Character> {

	@Override
	public boolean maps(Property<?> property) {
		return Character.class == property.type()
				|| Character.TYPE == property.type();
	}

	public String type(Property<Character> property, Repository repository) {
		return "TEXT";
	}

	@Override
	public void bind(Property<Character> property, Repository repository,
			SQLiteStatement statement, int index) {
		Character value = property.getInternal();
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindString(index, Character.toString(value));
		}
	}

	@Override
	public void retrieve(Property<Character> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.setInternal(null);
		} else {
			String string = cursor.getString(index);
			if (string.length() != 1) {
				throw new RepositoryException(
						"character must have length 1, is '" + string + "'");
			}
			property.setInternal(string.charAt(0));
		}
	}

	@Override
	public String argument(Property<Character> newParam, Repository repository,
			Character value) {
		return Character.toString(value);
	}
}
