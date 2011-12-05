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
import propoid.db.Order;
import propoid.db.Repository;
import propoid.db.SQL;
import android.database.Cursor;

/**
 * Index a type of {@link Propoid}s.
 */
public class Index extends Operation {

	public Index(Repository repository) {
		super(repository);
	}

	public void now(Propoid propoid, boolean unique, Order... ordering) {

		String name = name(ordering);

		SQL sql = new SQL();
		sql.raw("CREATE");
		if (unique) {
			sql.raw(" UNIQUE");
		}
		sql.raw(" INDEX ");
		sql.escaped(name);
		sql.raw(" ON ");
		sql.escaped(repository.naming.toTable(repository, propoid.getClass()));
		sql.raw(" (");
		for (Order order : ordering) {
			sql.separate(", ");
			sql.raw(order.toString(repository));
		}
		sql.raw(")");

		String existing = existing(name);
		if (existing != null) {
			if (existing.equals(sql.toString())) {
				// already indexed
				return;
			}

			drop(name);
		}

		repository.getDatabase().execSQL(sql.toString());
	}

	private String name(Order[] ordering) {
		SQL sql = new SQL();

		for (Order order : ordering) {
			sql.separate(":");
			sql.raw(order.getProperty().name());
		}

		return sql.toString();
	}

	private String existing(String name) {
		SQL sql = new SQL();
		sql.raw("SELECT * FROM sqlite_master WHERE type = 'index' AND name = ?");

		Cursor cursor = repository.getDatabase().rawQuery(sql.toString(),
				new String[] { name });
		try {
			if (cursor.moveToNext()) {
				return cursor.getString(4);
			} else {
				return null;
			}
		} finally {
			cursor.close();
		}
	}

	private void drop(String name) {
		SQL sql = new SQL();
		sql.raw("DROP INDEX ");
		sql.escaped(name);
		repository.getDatabase().execSQL(sql.toString());
	}
}