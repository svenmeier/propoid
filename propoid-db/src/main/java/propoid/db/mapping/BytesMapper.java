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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import propoid.core.Property;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

/**
 * A mapper for {@code byte[]} properties.
 */
public class BytesMapper implements Mapper<byte[]> {

	@Override
	public boolean maps(Property<?> property) {
		Type type = property.type();
		return type instanceof GenericArrayType
				&& ((GenericArrayType) type).getGenericComponentType() == Byte.TYPE;
	}

	public String type(Property<byte[]> property, Repository repository) {
		return "BLOB";
	}

	@Override
	public void bind(Property<byte[]> property, Repository repository,
			SQLiteStatement statement, int index) {
		byte[] value = property.getInternal();
		if (value == null) {
			statement.bindNull(index);
		} else {
			statement.bindBlob(index, value);
		}
	}

	@Override
	public void retrieve(Property<byte[]> property, Repository repository,
			Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			property.setInternal(null);
		} else {
			property.setInternal(cursor.getBlob(index));
		}
	}

	@Override
	public String argument(Property<byte[]> newParam, Repository repository,
			byte[] value) {
		throw new RepositoryException("blob arguments not supported");
	}
}
