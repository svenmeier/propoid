package propoid.util.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * A service which accepts {@link ServiceListener}s.
 */
public abstract class ListenableService<T extends ServiceListener> extends
		Service {

	private Binder binder = new Binder();

	private List<T> listeners = new ArrayList<T>();

	/**
	 * Notification of a registered listener.
	 */
	public void onRegistered(T listener) {
	}

	/**
	 * Notification of a deregistered listener.
	 */
	public void onDeregistered(T listener) {
	}

	/**
	 * Get all listeners.
	 */
	public Iterable<T> listeners() {
		return listeners;
	}

	@Override
	public final IBinder onBind(Intent intent) {
		return binder;
	}

	class Binder extends android.os.Binder {
		void register(T listener) {
			listeners.add(listener);
			onRegistered(listener);
		}

		void deregister(T listener) {
			listeners.remove(listener);
			onDeregistered(listener);
		}
	}
}