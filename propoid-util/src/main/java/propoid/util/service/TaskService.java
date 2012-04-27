package propoid.util.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

/**
 * A service mediating between {@link Task}s and {@link TaskObserver}s.
 * 
 * @see #resolveTask(Class, Intent)
 */
public abstract class TaskService<L extends TaskObserver> extends Service {

	private ObserverBinder binder;

	private List<L> observers = new ArrayList<L>();

	private Map<Class<? extends Task>, Constructor<?>> constructors = new HashMap<Class<? extends Task>, Constructor<?>>();

	/**
	 * Currently executed tasks.
	 */
	private List<Execution> executions = new ArrayList<Execution>();

	private ExecutorService executor;

	private Handler handler;

	protected TaskService() {
		this(new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>()));
	}

	protected TaskService(ExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		handler = new Handler(getMainLooper());

		binder = new ObserverBinder();
	}

	@Override
	public void onDestroy() {
		executor.shutdown();

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getAction() != null) {
			Task task = resolveTask(intent);
			if (task != null) {
				schedule(task);
			}
		}
		return START_NOT_STICKY;
	}

	/**
	 * Schedule a new {@link Task}.
	 * 
	 * @param task
	 *            task to schedule
	 */
	public synchronized final void schedule(Task task) {
		for (int w = 0; w < executions.size(); w++) {
			Execution candidate = executions.get(w);

			if (candidate.task.includes(task)) {
				// included
				return;
			}
		}

		Execution execution = new Execution(task);

		executions.add(execution);

		executor.submit((Callable<Void>) execution);
	}

	synchronized void deschedule(Execution execution) {
		executions.remove(execution);

		if (execution.successors != null) {
			for (Task successor : execution.successors) {
				schedule(successor);
			}
		}
	}

	/**
	 * Resolve a task for the given intent.
	 * 
	 * @param intent
	 *            the initiating intent
	 * @return
	 * 
	 * @see #schedule(Task)
	 */
	@SuppressWarnings("unchecked")
	protected Task resolveTask(Intent intent) {
		String action = intent.getAction();

		try {
			Class<? extends Task> clazz = (Class<? extends Task>) Class
					.forName(action);

			Constructor<?> constructor = getConstructor(clazz);

			if (constructor.getParameterTypes().length == 1) {
				return (Task) constructor.newInstance(this);
			} else {
				return (Task) constructor.newInstance(this, intent);
			}
		} catch (Exception ex) {
			onInvalidAction(action, ex);
		}

		return null;
	}

	protected void onInvalidAction(String action, Exception ex) {
		Log.i("propoid-util", "invalid action '" + action + "'");
	}

	private Constructor<?> getConstructor(Class<? extends Task> clazz) {
		Constructor<?> constructor = constructors.get(clazz);

		if (constructor == null) {
			try {
				constructor = clazz.getDeclaredConstructor(getClass(),
						Intent.class);

				constructors.put(clazz, constructor);
			} catch (Exception ex) {
			}
		}

		if (constructor == null) {
			try {
				constructor = clazz.getDeclaredConstructor(getClass());

				constructors.put(clazz, constructor);
			} catch (Exception ex) {
			}
		}

		return constructor;
	}

	/**
	 * Hook method to be notified of a newly subscribed {@link TaskObserver}s.
	 * 
	 * @param observer
	 */
	protected void onSubscribed(L observer) {
	}

	/**
	 * Hook method to be notified of a newly unsubscribed {@link TaskObserver}s.
	 * 
	 * @param observer
	 */
	protected void onUnsubscribed(L observer) {
	}

	/**
	 * Hook method to be notified of an unhandled exception.
	 */
	protected void onUnhandledException(Exception ex) {
		Log.e("propoid-util", "unhandled exception" + ex.getMessage() + "'", ex);
	}

	@Override
	public final IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * A schedule of a {@link Task}.
	 */
	class Execution implements Callable<Void>, Runnable {

		Task task;

		boolean publishing;

		/**
		 * Optional successive task.
		 * 
		 * @see #append(Task)
		 */
		List<Task> successors;

		public Execution(Task task) {
			this.task = task;

			task.schedule = this;
		}

		/**
		 * Called by the {@link ExecutorService}.
		 */
		@Override
		public Void call() throws Exception {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

			try {
				task.onExecute();
			} catch (Exception ex) {
				onUnhandledException(ex);
			}

			deschedule(this);

			return null;
		}

		/**
		 * Called by the {@link Task}.
		 */
		public synchronized void publish() {
			if (publishing) {
				throw new IllegalStateException("already publishing");
			}
			publishing = true;

			handler.post(this);

			while (publishing) {
				try {
					wait();
				} catch (InterruptedException interrupted) {
				}
			}
		}

		/**
		 * Called by the {@link Handler}.
		 */
		@Override
		public synchronized void run() {
			task.onPublish();

			publishing = false;

			notifyAll();
		}

		public void append(Task successor) {
			if (successors == null) {
				successors = new ArrayList<Task>();
			}
			successors.add(successor);
		}

	}

	class ObserverBinder extends Binder {

		void subscribe(L observer) {
			synchronized (observers) {
				observers.add(observer);
			}

			onSubscribed(observer);
		}

		void unsubscribe(L observer) {
			synchronized (observers) {
				observers.remove(observer);
			}

			onUnsubscribed(observer);
		}
	}

	/**
	 * A task.
	 */
	public abstract class Task {

		Execution schedule;

		/**
		 * Does this task include the other task.
		 * <p>
		 * Overriden methods may
		 * <ul>
		 * <li>return {@code false} for unrelated tasks</li>
		 * <li>drop the task silently and return {@code true}, e.g. if its
		 * purpose is already served by this task</li>
		 * <li>append the task and return {@code true} to let it be scheduled
		 * after this task has finished</li>
		 * </ul>
		 * 
		 * @param other
		 * 
		 * @see #append(Task)
		 */
		public boolean includes(Task other) {
			return false;
		}

		/**
		 * Append another task to be scheduled after this one.
		 * 
		 * @param other
		 *            task to append
		 */
		public final void append(Task other) {
			if (schedule == null) {
				throw new IllegalStateException(
						"call append() from onExecute() only");
			}

			schedule.append(other);
		}

		/**
		 * Execute the actual task.
		 */
		protected void onExecute() {
		}

		/**
		 * Initiate publishing.
		 * 
		 * @see #onPublish()
		 */
		public final void publish() {
			if (schedule == null) {
				throw new IllegalStateException(
						"call publish() from onExecute() only");
			}

			schedule.publish();
		}

		/**
		 * Publish, forwards to {@link #onPublish(TaskObserver)} for each
		 * subscribed observer.
		 */
		protected void onPublish() {
			for (L observer : observers) {
				onPublish(observer);
			}
		}

		/**
		 * Publish to the given observer.
		 * 
		 * @param observer
		 *            observer
		 */
		protected void onPublish(L observer) {
		}
	}

	/**
	 * Utility method to create an {@link Intent} for the given service's
	 * {@link Task}.
	 * 
	 * @param context
	 *            context of intent
	 * @param service
	 *            service
	 * @param action
	 *            action of intent
	 */
	public static <L extends TaskObserver> Intent createIntent(Context context,
			Class<? extends TaskService<L>> service,
			Class<? extends TaskService<L>.Task> action) {
		Intent intent = new Intent(context, service);
		intent.setAction(action.getName());
		return intent;
	}
}