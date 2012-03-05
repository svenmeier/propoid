package propoid.db.schema;

import java.util.ArrayList;
import java.util.List;

import propoid.db.SQL;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Column {
	public final String name;

	public final String type;

	public Column(String name, String type) {
		this.name = name;
		this.type = "_id".equals(name) ? type + " PRIMARY KEY" : type;
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
				columns.add(new Column(cursor.getString(1), cursor.getString(2)));
			}

			return columns;
		} finally {
			cursor.close();
		}
	}
}
