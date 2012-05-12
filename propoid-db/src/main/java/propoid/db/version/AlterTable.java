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

import propoid.db.SQL;
import propoid.db.schema.Column;
import propoid.db.version.alter.AlterColumn;
import propoid.db.version.alter.CreateColumn;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Alter of a single table.
 */
public class AlterTable implements Upgrade {

	private String oldName;
	private String newName;

	private List<AlterColumn> alters = new ArrayList<AlterColumn>();

	public AlterTable(String name, AlterColumn... column) {
		this(name, name, column);
	}

	public AlterTable(String oldName, String newName, AlterColumn... alters) {
		this.oldName = oldName;
		this.newName = newName;

		add(alters);
	}

	public void add(AlterColumn... alters) {
		for (AlterColumn column : alters) {
			this.alters.add(column);
		}
	}

	@Override
	public void apply(SQLiteDatabase database) throws SQLException {

		List<Column> existingColumns = Column.get(oldName, database);

		if (alters.isEmpty()) {
			if (!existingColumns.isEmpty()) {
				renameTable(database, oldName, newName);
			}
		} else {
			if (existingColumns.isEmpty()) {
				createTable(database, existingColumns, newName);
			} else {
				String temp = oldName + "_" + newName;

				createTable(database, existingColumns, temp);

				moveRows(database, existingColumns, oldName, temp);

				dropTable(database, oldName);

				renameTable(database, temp, newName);
			}
		}
	}

	private void createTable(SQLiteDatabase database,
			List<Column> existingColumns, String name) {

		SQL sql = new SQL();

		sql.raw("CREATE TABLE ");
		sql.escaped(name);
		sql.raw(" (");
		for (Column column : existingColumns) {
			Column altered = alter(column);
			if (altered != null) {
				sql.separate(", ");
				sql.raw(altered.ddl());
			}
		}
		for (AlterColumn alter : alters) {
			if (alter instanceof CreateColumn) {
				sql.separate(", ");
				sql.raw(alter.alter(null).ddl());
			}
		}
		sql.raw(")");

		database.execSQL(sql.toString());
	}

	private Column alter(Column column) {
		for (AlterColumn alter : alters) {
			if (alter.alters(column)) {
				return alter.alter(column);
			}
		}
		return column;
	}

	private void moveRows(SQLiteDatabase database,
			List<Column> existingColumns, String from, String to) {

		SQL sql = new SQL();

		sql.raw("INSERT INTO ");
		sql.escaped(to);
		sql.raw(" (");
		for (Column column : existingColumns) {
			Column altered = alter(column);
			if (altered != null) {
				sql.separate(", ");
				sql.escaped(altered.name);
			}
		}
		sql.raw(")");

		sql.separate(" ");

		sql.raw(" SELECT ");
		for (Column column : existingColumns) {
			Column altered = alter(column);
			if (altered != null) {
				sql.separate(", ");
				sql.escaped(column.name);
			}
		}
		sql.raw(" FROM ");
		sql.escaped(from);

		database.execSQL(sql.toString());
	}

	private void dropTable(SQLiteDatabase database, String name) {
		SQL sql = new SQL();

		sql.raw("DROP TABLE ");
		sql.escaped(name);

		database.execSQL(sql.toString());
	}

	private void renameTable(SQLiteDatabase database, String from, String to) {
		SQL sql = new SQL();

		sql.raw("ALTER TABLE ");
		sql.escaped(from);
		sql.raw(" RENAME TO ");
		sql.escaped(to);

		database.execSQL(sql.toString());
	}
}