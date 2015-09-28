package propoid.db.service;

import propoid.db.Locator;
import propoid.db.Repository;
import propoid.db.Setting;
import propoid.db.locator.FileLocator;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Base implementation for a service based {@link Repository}.
 * <p>
 * Register this or a subclass in your application's manifest:
 * 
 * <pre>
 * {@code
 *   <service android:name="propoid.db.service.RepositoryService" />
 * }
 * </pre>
 * 
 * @see RepositoryConnection
 */
public class RepositoryService extends Service {

	private final IBinder binder = new RepositoryBinder();

	Repository repository;

	@Override
	public void onCreate() {
		super.onCreate();

		repository = new Repository(this, getLocator(), getSettings());
	}

	@Override
	public void onDestroy() {
		repository.close();
		repository = null;

		super.onDestroy();
	}

	/**
	 * Override if you want to open the repository from a different location,
	 * i.e. "repository" in the package's data folder.
	 */
	protected Locator getLocator() {
		return new FileLocator(getApplicationContext(), "repository");
	}

	/**
	 * Override if you want to use non-default {@link Setting}s.
	 */
	protected Setting[] getSettings() {
		return new Setting[0];
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	final class RepositoryBinder extends Binder {
		public final RepositoryService service = RepositoryService.this;
	}
}