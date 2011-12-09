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
package propoid.db.operation;

import propoid.core.Propoid;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.SQL;
import propoid.db.aspect.Row;
import android.database.Cursor;

/**
 * Refresh a single {@link Propoid}.
 */
public class Refresh extends Operation {

	public Refresh(Repository repository) {
		super(repository);
	}

	public void now(Propoid propoid) {
		long id = Row.getID(propoid);
		if (id == Row.TRANSIENT) {
			throw new RepositoryException("cannot refesh transient propoid");
		}

		final SQL sql = new SQL();

		sql.raw("SELECT * FROM ");
		sql.escaped(repository.naming.table(repository, propoid.getClass()));
		sql.raw(" WHERE _id = ?");

		Cursor cursor = repository.getDatabase().rawQuery(sql.toString(),
				new String[] { Long.toString(id) });
		try {
			if (!cursor.moveToFirst()) {
				throw new RepositoryException("unkown propoid " + id);
			}

			retrieve(cursor, propoid);
		} finally {
			cursor.close();
		}
	}
}