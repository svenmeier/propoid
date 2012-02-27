package propoid.util.service;

import java.util.ArrayList;
import java.util.List;

import android.os.Binder;

/**
 * A binder supporting {@link ServiceListener}s.
 * 
 * <pre>
 * {@code
 *  public class FooService extends Service {
 *    private FooBinder binder = new FooBinder();
 *    
 *    public IBinder onBind(Intent intent) {
 *      return binder;
 *    }
 *    
 *    private void barChanged() {
 * 	    for (FooListener listener : binder.listeners()) {
 * 		  listener.onBar();
 * 		}
 *    }
 *    
 *    private class FooBinder extends ServiceListenerBinder<FooListener> {
 * 		public void onRegistered(FooListener listener) {
 * 			listener.onBar();
 * 		}
 *    }
 *  }
 * </pre>
 */
public abstract class ServiceListenerBinder<T extends ServiceListener> extends
		Binder {

	private List<T> listeners = new ArrayList<T>();

	/**
	 * Notification of a registered listener.
	 */
	protected void onRegistered(T listener) {
	}

	/**
	 * Notification of a deregistered listener.
	 */
	protected void onDeregistered(T listener) {
	}

	/**
	 * Get all listeners.
	 */
	public Iterable<T> listeners() {
		return listeners;
	}

	void register(T listener) {
		listeners.add(listener);
		onRegistered(listener);
	}

	void deregister(T listener) {
		listeners.remove(listener);
		onDeregistered(listener);
	}
}