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
import propoid.db.version.alter.DropColumn;
import propoid.db.version.alter.RenameColumn;
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
		if (existingColumns.isEmpty()) {
			// doesn't exist yet
			return;
		}

		if (alters.isEmpty()) {
			renameNew(database, oldName);
		} else {
			String temp = oldName + "_" + newName;

			createNew(database, existingColumns, temp);

			moveOldToNew(database, existingColumns, temp);

			dropOld(database);

			renameNew(database, temp);
		}
	}

	private void createNew(SQLiteDatabase database,
			List<Column> existingColumns, String temp) {

		SQL sql = new SQL();

		sql.raw("CREATE TABLE ");
		sql.escaped(temp);
		sql.raw(" (");
		for (Column column : existingColumns) {
			AlterColumn alter = alter(column);
			if (alter instanceof DropColumn) {
				continue;
			}

			sql.separate(", ");
			if (alter instanceof RenameColumn) {
				sql.escaped(((RenameColumn) alter).newName);
				sql.raw(" ");
				sql.raw(column.type);
			} else {
				sql.escaped(column.name);
				sql.raw(" ");
				sql.raw(column.type);
			}
		}
		sql.raw(")");

		database.execSQL(sql.toString());
	}

	private AlterColumn alter(Column column) {
		for (AlterColumn alter : alters) {
			if (alter.oldName.equals(column.name)) {
				return alter;
			}
		}
		return null;
	}

	private void moveOldToNew(SQLiteDatabase database,
			List<Column> existingColumns, String temp) {

		SQL sql = new SQL();

		sql.raw("INSERT INTO ");
		sql.escaped(temp);
		sql.raw(" (");
		for (Column column : existingColumns) {
			AlterColumn alter = alter(column);
			if (alter instanceof DropColumn) {
				continue;
			}

			sql.separate(", ");
			if (alter instanceof RenameColumn) {
				sql.escaped(((RenameColumn) alter).newName);
			} else {
				sql.escaped(column.name);
			}
		}
		sql.raw(")");

		sql.separate(" ");

		sql.raw(" SELECT ");
		for (Column column : existingColumns) {
			AlterColumn alter = alter(column);
			if (alter instanceof DropColumn) {
				continue;
			}

			sql.separate(", ");
			sql.escaped(column.name);
		}
		sql.raw(" FROM ");
		sql.escaped(oldName);

		database.execSQL(sql.toString());
	}

	private void dropOld(SQLiteDatabase database) {
		SQL sql = new SQL();

		sql.raw("DROP TABLE ");
		sql.escaped(oldName);

		database.execSQL(sql.toString());
	}

	private void renameNew(SQLiteDatabase database, String temp) {
		SQL sql = new SQL();

		sql.raw("ALTER TABLE ");
		sql.escaped(temp);
		sql.raw(" RENAME TO ");
		sql.escaped(newName);

		database.execSQL(sql.toString());
	}
}