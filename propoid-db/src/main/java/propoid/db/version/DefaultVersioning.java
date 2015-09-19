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
package propoid.db.version;

import java.util.ArrayList;
import java.util.List;

import propoid.db.Versioning;
import android.database.sqlite.SQLiteDatabase;

/**
 * Default versioning defined by a list of {@link Upgrade}s. Each upgrade
 * increments the database's version by one.
 * 
 * @see #add(Upgrade)
 */
public class DefaultVersioning implements Versioning {

	private List<Upgrade> upgrades = new ArrayList<Upgrade>();

	/**
	 * Add an upgrade.
	 */
	public void add(Upgrade upgrade) {
		upgrades.add(upgrade);
	}

	/**
	 * Applies all {@link Upgrade}s with index smaller than the current databse
	 * version.
	 * <p>
	 * Each {@link Upgrade} is applied in a separate transaction to keep the
	 * database in a consistent state in case of a failure.
	 * 
	 * @param database
	 *            database to upgrade
	 */
	@Override
	public void upgrade(SQLiteDatabase database) {
		int version = database.getVersion();

		while (version < upgrades.size()) {
			database.beginTransaction();
			try {
				upgrades.get(version).apply(database);

				version++;

				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}

			database.setVersion(version);
		}
	}
}
