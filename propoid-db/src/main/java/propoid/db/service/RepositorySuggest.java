package propoid.db.service;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import propoid.core.Propoid;
import propoid.db.Locator;
import propoid.db.Match;
import propoid.db.Range;
import propoid.db.Reference;
import propoid.db.Repository;
import propoid.db.RepositoryException;
import propoid.db.Setting;
import propoid.db.aspect.Row;
import propoid.db.locator.FileLocator;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

/**
 * A {@link ContentProvider} for search suggestions based on a
 * {@link Repository}.
 * <p>
 * Create a subclass and register it in your application's manifest:
 * 
 * <pre>
 * {@code
 * <provider
 *   android:name="your.Suggest"
 *   android:authorities="your.suggest" 
 * />
 * }
 * </pre>
 * 
 * Define your search in "res/xml/your_search.xml":
 * 
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="utf-8"?>
 * <searchable xmlns:android="http://schemas.android.com/apk/res/android"
 *   android:label="@string/your_app_name"
 *   android:hint="@string/your_search_hint"
 *   android:includeInGlobalSearch="true"
 *   android:searchSettingsDescription="@string/your_search_description"
 *   android:searchSuggestAuthority="your.suggest"
 *   android:searchSuggestIntentAction="android.intent.action.VIEW"
 * />
 * }
 * </pre>
 * 
 * Define your activity to handles searches:
 * 
 * <pre>
 * {@code
 * <activity android:name="your.Activity">
 *   <intent-filter>
 *     <action android:name="android.intent.action.SEARCH" />
 *   </intent-filter>
 *   <meta-data
 *     android:name="android.app.searchable"
 *     android:resource="@xml/your_search"
 *   />
 * </activity>
 * }
 * </pre>
 * 
 * Optionally make your activity the default searchable for your application
 * (i.e. a search is started inside your application when pressing the search
 * button):
 * 
 * <pre>
 * {@code
 * <meta-data
 *   android:name="android.app.default_searchable"
 *   android:value="your.Activity"
 * />
 * }
 * </pre>
 * 
 * Let your activity handle {@link Intent#ACTION_SEARCH} with the search query
 * entered by the user, or the configured action with the {@link Reference}
 * selected by the user.
 * 
 * <pre>
 * {@code
 * public void onCreate(Bundle state) {
 *   super.onCreate(state);
 * 
 *   if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
 *     String query = RepositorySuggest.getQuery(getIntent());
 *   } else if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
 *     Reference<Foo> reference = RepositorySuggest.getReference(getIntent());
 *   } else {
 *     // other action
 *   }
 * }
 * }
 * </pre>
 * 
 * @see Repository#lookup(Reference)
 * @see #query(Repository, String)
 */
public abstract class RepositorySuggest<P extends Propoid> extends
		ContentProvider {

	private Repository repository;

	@Override
	public boolean onCreate() {
		repository = new Repository(getLocator(), getSettings());

		return true;
	}

	/**
	 * Override if you want to open the repository from a different location,
	 * i.e. "repository" in the package's data folder.
	 */
	protected Locator getLocator() {
		return new FileLocator(getContext(), "repository");
	}

	/**
	 * Override if you want to use non-default {@link Setting}s.
	 */
	protected Setting[] getSettings() {
		return new Setting[0];
	}

	/**
	 * Hook method for subclasses to perform the actual query.
	 * 
	 * @param repository
	 *            the repository to query
	 * @param query
	 *            the query string
	 * @return match
	 * 
	 * @see Repository#query(Propoid, propoid.db.Where)
	 */
	protected abstract Match<P> query(Repository repository, String query);

	/**
	 * Get the first text to be displayed for the given propoid.
	 * 
	 * @see SearchManager#SUGGEST_COLUMN_TEXT_1
	 */
	protected abstract String getText1(P propoid);

	/**
	 * Get a second optional text to be displayed for the given propoid.
	 * 
	 * @see SearchManager#SUGGEST_COLUMN_TEXT_2
	 */
	protected String getText2(P propoid) {
		return null;
	}

	/**
	 * Get a first optional icon to be displayed for the given propoid.
	 * 
	 * @see SearchManager#SUGGEST_COLUMN_ICON_1
	 */
	protected String getIcon1(P propoid) {
		return null;
	}

	/**
	 * Get a second optional icon to be displayed for the given propoid.
	 * 
	 * @see SearchManager#SUGGEST_COLUMN_ICON_2
	 */
	protected String getIcon2(P propoid) {
		return null;
	}

	/**
	 * Should a shortcut be created for the given propoid, default is
	 * {@code true}.
	 * 
	 * @see SearchManager#SUGGEST_COLUMN_SHORTCUT_ID
	 */
	protected boolean createShortcut(P propoid) {
		return true;
	}

	@Override
	public final Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		String path = uri.getPath();

		if (path.startsWith("/" + SearchManager.SUGGEST_URI_PATH_QUERY)) {
			String query = uri.getLastPathSegment();
			String limit = uri.getQueryParameter("limit");

			Match<P> match = query(repository, query);
			Range range = (limit == null) ? Range.all() : Range.limit(Integer
					.parseInt(limit));

			return new MatchCursor(match, range);
		} else if (path.startsWith("/"
				+ SearchManager.SUGGEST_URI_PATH_SHORTCUT)) {
			Reference<P> reference = Reference.fromString(uri
					.getLastPathSegment());
			if (reference != null) {
				try {
					return new ReferenceCursor(reference);
				} catch (RepositoryException ex) {
				}
			}
		}
		return null;
	}

	/**
	 * @throws SQLException
	 *             always
	 */
	@Override
	public String getType(Uri uri) {
		throw new SQLException();
	}

	/**
	 * @throws SQLException
	 *             always
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new SQLException();
	}

	/**
	 * @throws SQLException
	 *             always
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new SQLException();
	}

	/**
	 * @throws SQLException
	 *             always
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new SQLException();
	}

	/**
	 * Utility method to get the entered query from the given intent.
	 * 
	 * @see Intent#ACTION_SEARCH
	 * @see SearchManager#QUERY
	 */
	public static String getQuery(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			return intent.getStringExtra(SearchManager.QUERY);
		}

		return null;
	}

	/**
	 * Utility method to get the selected reference from the given intent.
	 * 
	 * @see Intent#getData()
	 */
	public static <P extends Propoid> Reference<P> getReference(Intent intent) {
		Uri data = intent.getData();
		if (data != null) {
			return Reference.fromUri(data);
		}

		return null;
	}

	private class ReferenceCursor extends PropoidCursor {

		private P propoid;

		public ReferenceCursor(Reference<P> reference) {
			this.propoid = repository.lookup(reference);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		protected P getPropoid() {
			return propoid;
		}
	}

	private class MatchCursor extends PropoidCursor {

		private List<P> list;

		public MatchCursor(Match<P> match, Range range) {
			this.list = match.list(range);
		}

		@Override
		public void close() {
			try {
				((Closeable) list).close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

			super.close();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		protected P getPropoid() {
			return list.get(getPosition());
		}
	}

	private abstract class PropoidCursor extends AbstractCursor {

		private String[] columnNames;

		protected PropoidCursor() {
			columnNames = new String[] { "_id",
					SearchManager.SUGGEST_COLUMN_TEXT_1,
					SearchManager.SUGGEST_COLUMN_TEXT_2,
					SearchManager.SUGGEST_COLUMN_ICON_1,
					SearchManager.SUGGEST_COLUMN_ICON_2,
					SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
					SearchManager.SUGGEST_COLUMN_INTENT_DATA };
		}

		@Override
		public String[] getColumnNames() {
			return columnNames;
		}

		protected abstract P getPropoid();

		@Override
		public long getLong(int column) {
			if (column == 0) {
				return Row.getID(getPropoid());
			}
			throw new SQLException();
		}

		@Override
		public String getString(int column) {
			P propoid = getPropoid();
			if (column == 0) {
				return "" + getLong(0);
			} else if (column == 1) {
				return getText1(propoid);
			} else if (column == 2) {
				return getText2(propoid);
			} else if (column == 3) {
				return getIcon1(propoid);
			} else if (column == 4) {
				return getIcon2(propoid);
			} else if (column == 5) {
				if (createShortcut(propoid)) {
					return new Reference<P>(propoid).toString();
				} else {
					return SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT;
				}
			} else if (column == 6) {
				return new Reference<P>(propoid).toString();
			}
			throw new SQLException();
		}

		@Override
		public int getInt(int column) {
			throw new SQLException();
		}

		@Override
		public short getShort(int column) {
			throw new SQLException();
		}

		@Override
		public float getFloat(int column) {
			throw new SQLException();
		}

		@Override
		public double getDouble(int column) {
			throw new SQLException();
		}

		@Override
		public boolean isNull(int column) {
			return false;
		}
	}
}