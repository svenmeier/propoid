package propoid.db.service;

import propoid.core.Property;
import propoid.core.Propoid;
import propoid.db.Locator;
import propoid.db.Match;
import propoid.db.Repository;
import propoid.db.Setting;
import propoid.db.locator.FileLocator;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * A {@link ContentProvider} for search suggestions based on a
 * {@link Repository}.
 * <p>
 * Register a subclass in your application's manifest:
 * 
 * <pre>
 * {@code
 *   <provider
 *     android:name="your.Suggest"
 *     android:authorities="your.authority" 
 *   />
 * }
 * </pre>
 * 
 * Define your search suggestions in "res/xml/your_suggest":
 * 
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="utf-8"?>
 * <searchable xmlns:android="http://schemas.android.com/apk/res/android"
 *   android:label="@string/your_app_name"
 *   android:hint="@string/search_hint"
 *   android:includeInGlobalSearch="true"
 *   android:searchSuggestAuthority="your.authority"
 *   android:searchSettingsDescription="@string/search_description"
 * />
 * }
 * </pre>
 * 
 * Define your activity which handles searches:
 * 
 * <pre>
 * {@code
 *   <activity android:name="your.Activity">
 *     <intent-filter>
 *       <action android:name="android.intent.action.SEARCH" />
 *     </intent-filter>
 *     <meta-data
 *       android:name="android.app.searchable"
 *       android:resource="@xml/your_suggest"
 *     />
 *   </activity>
 * }
 * </pre>
 * 
 * Let your activity handle two possibly intents:
 * <ul>
 * <li>a search was initiated from the search manager, resulting in a query
 * being passed to your activity</li>
 * <li>a propoid was selected by the search manager, resulting in a reference
 * being passed to your activity</li>
 * </ul>
 * 
 * <pre>
 * {@code
 * 	public void onCreate(Bundle state) {
 * 	  super.onCreate(state);
 * 
 * 	  String query = intent.getStringExtra(SearchManager.QUERY);
 * 	  Reference&lt;Fooe&gt; reference = Reference.fromString(intent
 * 				.getDataString());
 * }
 * </pre>
 * 
 * @see #query(Repository, String)
 */
public abstract class RepositorySuggest<P extends Propoid> extends
		ContentProvider {

	private Repository repository;

	private Property<?> text1;

	private Property<?> text2;

	protected RepositorySuggest(Property<?> text1) {
		this(text1, null);
	}

	protected RepositorySuggest(Property<?> text1, Property<?> text2) {
		this.text1 = text1;
		this.text2 = text2;
	}

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

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		String query = uri.getLastPathSegment().toLowerCase();

		Match<P> match = query(repository, query);

		return match.suggest(text1, text2);
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

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
