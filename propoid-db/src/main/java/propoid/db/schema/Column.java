package propoid.db.schema;

import java.util.ArrayList;
import java.util.List;

import propoid.db.SQL;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Column {

	public static final String INTEGER = "INTEGER";

	public static final String TEXT = "TEXT";

	public final String name;

	public final String type;

	public final boolean notNull;

	public final String dfltValue;

	public final boolean pk;

	public Column(String name, String type, boolean notNull, String dfltValue,
			boolean pk) {
		this.name = name;
		this.type = type;
		this.notNull = notNull;
		this.dfltValue = dfltValue;
		this.pk = pk;
	}

	public static List<Column> get(String table, SQLiteDatabase database) {
		SQL sql = new SQL();
		sql.raw("PRAGMA table_info(");
		sql.escaped(table);
		sql.raw(")");

		Cursor cursor = database.rawQuery(sql.toString(), new String[0]);
		try {
			List<Column> columns = new ArrayList<Column>();

			while (cursor.moveToNext()) {
				columns.add(new Column(cursor.getString(1),
						cursor.getString(2), cursor.getInt(3) == 0 ? false
								: true, cursor.getString(4),
						cursor.getInt(5) == 0 ? false : true));
			}

			return columns;
		} finally {
			cursor.close();
		}
	}

	public static boolean exists(String table, SQLiteDatabase database) {
		SQL sql = new SQL();
		sql.raw("PRAGMA table_info(");
		sql.escaped(table);
		sql.raw(")");

		Cursor cursor = database.rawQuery(sql.toString(), new String[0]);
		try {
			return cursor.moveToNext();
		} finally {
			cursor.close();
		}
	}

	public String ddl() {
		SQL sql = new SQL();

		sql.escaped(name);
		sql.raw(" ");
		sql.raw(type);
		if (notNull) {
			sql.raw(" NOT NULL");
		}
		if (dfltValue != null) {
			sql.raw(" DEFAULT (");
			sql.raw(dfltValue);
			sql.raw(")");
		}
		if (pk) {
			sql.raw(" PRIMARY KEY");
		}

		return sql.toString();
	}
}
