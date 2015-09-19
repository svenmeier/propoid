package propoid.util.service;

import propoid.util.service.TaskService.ObserverBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * An observer of a {@link TaskService}.
 * 
 * <pre>
 * @code{
 * 	FooObserver observer = new FooObserver() {
 * 		public void onBar() {
 * 		}
 * 	};
 * 	observer.subscribe(context, FooService.class);
 * }
 * </pre>
 * 
 * An observer will always be notified on the main thread.
 */
@SuppressWarnings("rawtypes")
public abstract class TaskObserver implements ServiceConnection {

	private Context context;

	private ObserverBinder binder;

	/**
	 * Subscribe with the given {@link TaskService}.
	 */
	public void subscribe(Context context, Class<? extends TaskService> service) {
		if (this.context != null) {
			throw new IllegalStateException();
		}

		this.context = context;

		context.bindService(new Intent(context, service), this,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * Unsubsribe from the previously subscribed {@link TaskService}.
	 */
	@SuppressWarnings("unchecked")
	public void unsubscribe() {
		if (binder == null) {
			throw new IllegalStateException();
		}

		binder.unsubscribe(this);
		binder = null;

		context.unbindService(this);
		context = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onServiceConnected(ComponentName name, IBinder binder) {
		this.binder = (ObserverBinder) binder;

		this.binder.subscribe(this);
	}

	@Override
	public final void onServiceDisconnected(ComponentName name) {
	}
}
