package propoid.db.service;

import propoid.db.Repository;
import propoid.db.service.RepositoryService.RepositoryBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * A connection to a {@link RepositoryService}. Usage:
 * 
 * <pre>
 * {@code
 *   new RepositoryConnection() {
 *     public void onConnected(Repository repository) {
 *       // ... work with repository
 *     }
 *   }.bind(context, RepositoryService.class);
 * }
 * </pre>
 */
public class RepositoryConnection implements ServiceConnection {

	private Context context;

	/**
	 * Bind to the {@link RepositoryService}.
	 */
	public void bind(Context context, Class<? extends RepositoryService> clazz) {
		this.context = context;

		boolean successful = context.bindService(new Intent(context, clazz),
				this, Context.BIND_AUTO_CREATE);
		if (!successful) {
			throw new IllegalArgumentException("cannot bind to service "
					+ clazz.getName());
		}
	}

	/**
	 * Unbind from the {@link RepositoryService}.
	 */
	public void unbind() {
		context.unbindService(this);

		this.context = null;
	}

	public final void onServiceConnected(ComponentName className, IBinder binder) {
		Repository repository = ((RepositoryBinder) binder).service.repository;

		onConnected(repository);
	}

	public final void onServiceDisconnected(ComponentName className) {
		onDisconnected();
	}

	/**
	 * Hook method on connect to the repository.
	 */
	public void onConnected(Repository repository) {
	}

	/**
	 * Hook method on disconnect from the repository.
	 */
	public void onDisconnected() {
	}
}