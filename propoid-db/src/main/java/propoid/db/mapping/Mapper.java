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
 * A mapper for a {@link Property} to its database representation.
 */
public interface Mapper<T> {

	public boolean maps(Property<?> property);

	/**
	 * Get the database type.
	 * 
	 * @param property
	 *            the property
	 * @param repository
	 *            the repository
	 * @return database type
	 */
	public String type(Property<T> property, Repository repository);

	/**
	 * Bind a property to a statement.
	 * 
	 * @param property
	 *            the property
	 * @param repository
	 *            the repository
	 * @param statement
	 *            statement to bind to
	 * @param index
	 *            index on statement
	 */
	public void bind(Property<T> property, Repository repository,
			SQLiteStatement statement, int index);

	/**
	 * Retrieve a property from a cursor.
	 * 
	 * @param property
	 *            the property
	 * @param repository
	 *            the repository
	 * @param cursor
	 *            cursor to retrieve from
	 * @param index
	 *            index on cursor
	 */
	public void retrieve(Property<T> property, Repository repository,
			Cursor cursor, int index);

	/**
	 * Get a value as a query argument.
	 * 
	 * @param property
	 *            the property
	 * @param repository
	 *            the repository
	 * @param value
	 *            value to get as argument
	 * @return query argument
	 */
	public String argument(Property<T> property, Repository repository, T value);
}
