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
import java.util.Arrays;
import java.util.List;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Multiple upgrades compound in an upgrade.
 */
public class CompoundUpgrade implements Upgrade {

	private List<Upgrade> upgrades;

	public CompoundUpgrade() {
		upgrades = new ArrayList<Upgrade>();
	}

	public CompoundUpgrade(Upgrade... upgrades) {
		this(Arrays.asList(upgrades));
	}

	public CompoundUpgrade(List<Upgrade> upgrades) {
		this.upgrades = upgrades;
	}

	public void add(Upgrade upgrade) {
		this.upgrades.add(upgrade);
	}

	@Override
	public void apply(SQLiteDatabase database) throws SQLException {
		for (Upgrade upgrade : upgrades) {
			upgrade.apply(database);
		}
	}
}