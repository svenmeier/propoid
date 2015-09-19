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
import java.util.LinkedList;
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

		ColumnConsumer consumer = new ColumnConsumer();

		SQL sql = new SQL();

		sql.raw("CREATE TABLE ");
		sql.escaped(name);
		sql.raw(" (");
		for (Column column : existingColumns) {
			Column consumed = consumer.alter(column);
			if (consumed != null) {
				sql.separate(", ");
				sql.raw(consumed.ddl());
			}
		}
		for (AlterColumn alter : consumer.alters) {
			if (alter instanceof CreateColumn) {
				sql.separate(", ");
				sql.raw(alter.alter(null).ddl());
			} else {
				throw new SQLException(alter.toString());
			}
		}
		sql.raw(")");

		database.execSQL(sql.toString());
	}

	private void moveRows(SQLiteDatabase database,
			List<Column> existingColumns, String from, String to) {

		ColumnConsumer consumer = new ColumnConsumer();

		SQL insertInto = new SQL();
		SQL select = new SQL();

		insertInto.raw("INSERT INTO ");
		insertInto.escaped(to);
		insertInto.raw(" (");

		select.raw("SELECT ");

		for (Column column : existingColumns) {
			Column consumed = consumer.alter(column);
			if (consumed != null) {
				insertInto.separate(", ");
				insertInto.escaped(consumed.name);

				select.separate(", ");
				select.escaped(column.name);
			}
		}

		insertInto.raw(")");

		select.raw(" FROM ");
		select.escaped(from);

		database.execSQL(insertInto.toString() + select.toString());
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

	private class ColumnConsumer {
		public final List<AlterColumn> alters;

		public ColumnConsumer() {
			this.alters = new LinkedList<AlterColumn>(AlterTable.this.alters);
		}

		private Column alter(Column column) {
			for (AlterColumn alter : this.alters) {
				if (alter.alters(column)) {
					this.alters.remove(alter);

					return alter.alter(column);
				}
			}
			return column;
		}
	}
}