package propoid.util.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

/**
 * A generified connection to a service. Usage:
 * 
 * <pre>
 * {@code
 *   new GenericConnection<FooService.Binder>() {
 *     public void onConnected(FooService.Binder binder) {
 *       // ... work with binder
 *     }
 *   }.bind(context, FooService.class);
 * }
 * </pre>
 */
public abstract class GenericConnection<T extends Binder> implements
		ServiceConnection {

	private Context context;

	public void bind(Context context, Class<? extends Service> service) {
		this.context = context;

		context.bindService(new Intent(context, service), this,
				Context.BIND_AUTO_CREATE);
	}

	public void unbind() {
		context.unbindService(this);
		context = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onServiceConnected(ComponentName name, IBinder binder) {
		onConnected((T) binder);
	}

	@Override
	public final void onServiceDisconnected(ComponentName name) {
		onDisconnected();
	}

	protected void onConnected(T binder) {
	}

	protected void onDisconnected() {
	}
}
