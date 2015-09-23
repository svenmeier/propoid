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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propoid.core.Propoid;
import propoid.db.LookupException;
import propoid.db.Reference;
import propoid.db.References;
import propoid.db.Repository;
import propoid.db.SQL;
import android.database.Cursor;

/**
 * Lookup a {@link Propoid}.
 */
public class Lookup extends Operation {

	public Lookup(Repository repository) {
		super(repository);
	}

	public Propoid now(Reference<?> reference) {
		final SQL sql = new SQL();

		sql.raw("SELECT * FROM ");
		sql.escaped(repository.naming.table(repository, reference.type));
		sql.raw(" WHERE _id = ?");

		Cursor cursor = repository.getDatabase().rawQuery(sql.toString(),
				new String[] { Long.toString(reference.id) });
		try {
			if (!cursor.moveToFirst()) {
				throw new LookupException(reference);
			}

			return instantiate(reference.type, cursor);
		} finally {
			cursor.close();
		}
	}

	public List<Propoid> now(References<Propoid> references) {
		Map<Reference<Propoid>, Propoid> referenceToPropoid = new HashMap<Reference<Propoid>, Propoid>();

		if (!references.isEmpty()) {
			Class<? extends Propoid> type = references.iterator().next().type;

			final SQL sql = new SQL();
			String[] ids = new String[references.size()];

			sql.raw("SELECT * FROM ");
			sql.escaped(repository.naming.table(repository, type));
			sql.raw(" WHERE _id in (");
			int r = 0;
			for (Reference<Propoid> reference : references) {
				sql.separate(",");
				sql.raw("?");
				ids[r] = Long.toString(reference.id);
			}
			sql.raw(")");

			Cursor cursor = repository.getDatabase().rawQuery(sql.toString(),
					ids);
			try {
				while (cursor.moveToNext()) {
					Propoid propoid = instantiate(type, cursor);

					referenceToPropoid.put(new Reference<Propoid>(propoid),
							propoid);
				}
			} finally {
				cursor.close();
			}
		}

		List<Propoid> propoids = new ArrayList<Propoid>();
		for (Reference<Propoid> reference : references) {
			Propoid propoid = referenceToPropoid.get(reference);
			if (propoid != null) {
				propoids.add(propoid);
			}
		}
		return propoids;
	}
}