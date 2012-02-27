package propoid.util.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * A listener to a service with {@link ServiceListenerBinder}.
 * 
 * <pre>
 * {@code
 * 	FooListener listener = new FooListener() {
 *    public void onBar() {
 *    } 
 * 	};
 * 	listener.register(context, FooService.class);
 * }
 * </pre>
 */
@SuppressWarnings("rawtypes")
public abstract class ServiceListener implements ServiceConnection {

	private Context context;

	private ServiceListenerBinder binder;

	/**
	 * Register with the given {@link ListenableService}.
	 */
	public void register(Context context, Class<? extends Service> service) {
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
		this.binder = (ServiceListenerBinder) binder;

		this.binder.register(this);
	}

	@Override
	public final void onServiceDisconnected(ComponentName name) {
	}
}
