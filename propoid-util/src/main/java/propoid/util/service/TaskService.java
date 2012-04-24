package propoid.util.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * A service mediating between {@link Task}s and {@link TaskListener}s.
 * 
 * @see #resolveTask(Intent)
 */
public abstract class TaskService<L extends TaskListener> extends Service {

	private ListenerBinder binder;

	private List<L> listeners = new ArrayList<L>();

	private List<Execution> executions = new ArrayList<Execution>();

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 10,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	@Override
	public void onCreate() {
		super.onCreate();

		binder = new ListenerBinder();
	}

	@Override
	public void onDestroy() {
		executor.shutdown();

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			resolveTask(intent);
		}
		return START_NOT_STICKY;
	}

	public void schedule(Task task) {
		schedule(new Execution(task));
	}

	private synchronized void schedule(Execution execution) {
		for (int w = 0; w < executions.size(); w++) {
			Execution candidate = executions.get(w);

			if (candidate.task.add(execution.task)) {
				return;
			}
		}

		executions.add(execution);

		executor.execute(execution);
	}

	synchronized void deschedule(Execution wrapper) {
		executions.remove(wrapper);
	}

	/**
	 * Resolve a task for the given intent.
	 * 
	 * @see #schedule(Task)
	 */
	protected abstract void resolveTask(Intent intent);

	protected void onRegistered(L listener) {
	}

	protected void onDeregistered(L listener) {
	}

	@Override
	public final IBinder onBind(Intent intent) {
		return binder;
	}

	private class Execution implements Runnable {

		public Task task;

		public Execution(Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			task.onExecute();

			deschedule(Execution.this);
		}
	}

	/**
	 * A task.
	 */
	public abstract class Task {

		public boolean add(Task other) {
			return false;
		}

		public void onExecute() {
		}

		public final void notifyListeners() {
			for (final L listener : listeners) {
				listener.post(new Runnable() {
					@Override
					public void run() {
						onNotify(listener);
					}
				});
			}
		}

		public void onNotify(L listener) {
		}
	}

	class ListenerBinder extends Binder {

		void register(L listener) {
			listeners.add(listener);
			onRegistered(listener);
		}

		void deregister(L listener) {
			listeners.remove(listener);
			onDeregistered(listener);
		}
	}
}