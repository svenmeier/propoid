package propoid.test.db.version;

import java.util.List;

import propoid.db.schema.Column;
import propoid.db.version.AlterTable;
import propoid.db.version.alter.DropColumn;
import propoid.db.version.alter.RenameColumn;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

/**
 * Test for {@link AlterTable}.
 */
public class AlterTableTest extends ApplicationTestCase<Application> {

	private SQLiteDatabase database;

	public AlterTableTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		database = SQLiteDatabase.create(null);
	}

	@Override
	protected void tearDown() throws Exception {
		database.close();
	}

	public void test() throws Exception {
		database.execSQL("CREATE TABLE TEST_TABLE (_id PRIMARY KEY, COLUMN_1 TEXT, COLUMN_2 TEXT, COLUMN_3 TEXT)");
		database.execSQL("INSERT INTO TEST_TABLE VALUES (42, 'VALUE_1', 'VALUE_2', 'VALUE_3')");

		AlterTable alterTable = new AlterTable("TEST_TABLE", "TEST_TABLE_X");
		alterTable.add(new DropColumn("COLUMN_1"));
		alterTable.add(new RenameColumn("COLUMN_2", "COLUMN_2_X"));

		alterTable.apply(database);

		assertTrue(Column.get("TEST_TABLE", database).isEmpty());

		List<Column> columns = Column.get("TEST_TABLE_X", database);
		assertEquals(3, columns.size());
		assertEquals("_id", columns.get(0).name);
		assertEquals("COLUMN_2_X", columns.get(1).name);
		assertEquals("COLUMN_3", columns.get(2).name);

		Cursor cursor = database.rawQuery("SELECT * FROM TEST_TABLE_X",
				new String[0]);
		try {
			assertTrue(cursor.moveToNext());

			assertEquals("VALUE_2",
					cursor.getString(cursor.getColumnIndex("COLUMN_2_X")));
			assertEquals("VALUE_3",
					cursor.getString(cursor.getColumnIndex("COLUMN_3")));

			assertFalse(cursor.moveToNext());
		} catch (Exception ex) {
			cursor.close();
		}
	}
}