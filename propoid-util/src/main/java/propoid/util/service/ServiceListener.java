package propoid.util.service;

import propoid.util.service.ListenableService.Binder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * A listener to a {@link ListenableService}.
 */
@SuppressWarnings("rawtypes")
public abstract class ServiceListener implements ServiceConnection {

	private Context context;

	private Binder binder;

	/**
	 * Register with the given {@link ListenableService}.
	 */
	public void register(Context context,
			Class<? extends ListenableService> service) {
		if (this.context != null) {
			throw new IllegalStateException();
		}

		this.context = context;

		context.bindService(new Intent(context, service), this,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * Deregister from the previously registered {@link ListenableService}.
	 */
	@SuppressWarnings("unchecked")
	public void deregister() {
		if (binder == null) {
			throw new IllegalStateException();
		}

		binder.deregister(this);
		binder = null;

		context.unbindService(this);
		context = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onServiceConnected(ComponentName name, IBinder binder) {
		this.binder = (ListenableService.Binder) binder;

		this.binder.register(this);
	}

	@Override
	public final void onServiceDisconnected(ComponentName name) {
	}
}
