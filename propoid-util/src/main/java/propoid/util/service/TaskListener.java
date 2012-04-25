package propoid.util.service;

import propoid.util.service.TaskService.ListenerBinder;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

/**
 * A listener to a {@link TaskService}.
 * 
 * <pre>
 * @code{
 * 	FooListener listener = new FooListener() {
 * 		public void onBar() {
 * 		}
 * 	};
 * 	listener.register(context, FooTaskService.class);
 * }
 * </pre>
 */
@SuppressWarnings("rawtypes")
public abstract class TaskListener implements ServiceConnection {

	private Context context;

	private ListenerBinder binder;

	private Handler handler;

	/**
	 * Register with the given {@link ListenableService}.
	 */
	public void register(Context context, Class<? extends Service> service) {
		if (this.context != null) {
			throw new IllegalStateException();
		}

		this.context = context;

		this.handler = new Handler();

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

		handler = null;

		context.unbindService(this);
		context = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onServiceConnected(ComponentName name, IBinder binder) {
		this.binder = (ListenerBinder) binder;

		this.binder.register(this);
	}

	@Override
	public final void onServiceDisconnected(ComponentName name) {
	}

	/**
	 * Used by {@link TaskService} to post a notification back to the listener.
	 */
	void post(Runnable notification) {
		handler.post(notification);
	}
}