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
		schedule(intent);

		return START_NOT_STICKY;
	}

	@Override
	public final IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * Schedule an intent.
	 * 
	 * @param intent
	 *            intent to schedule
	 */
	protected void schedule(Intent intent) {
		if (intent != null && intent.getAction() != null) {
			Task task = resolveTask(intent);
			if (task != null) {
				schedule(task);
			}
		}
	}

	/**
	 * Schedule a new {@link Task}.
	 * 
	 * @param task
	 *            task to schedule
	 */
	protected synchronized final void schedule(Task task) {
		for (int w = 0; w < executions.size(); w++) {
			Execution candidate = executions.get(w);

			if (candidate.task.onScheduling(task)) {
				// handled by task
				return;
			}
		}

		Execution execution = new Execution(task);

		executions.add(execution);

		executor.submit((Callable<Void>) execution);
	}

	synchronized void deschedule(Execution execution) {
		executions.remove(execution);
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
		Task task;

		try {
			Class<? extends Task> clazz = (Class<? extends Task>) Class
					.forName(intent.getAction());

			Constructor<?> constructor = getConstructor(clazz);

			if (constructor.getParameterTypes().length == 1) {
				task = (Task) constructor.newInstance(this);
			} else {
				task = (Task) constructor.newInstance(this, intent);
			}

			onTaskResolved(intent, task);
		} catch (Throwable ex) {
			task = null;

			onTaskUnresolved(intent, ex);
		}

		return task;
	}

	/**
	 * Hook method for handling of a resolved task.
	 * 
	 * @param intent
	 *            intent that triggered the task
	 * @param task
	 *            resolved task
	 */
	protected void onTaskResolved(Intent intent, Task task) {
	}

	/**
	 * Hook method for handling tasks which could not be resolved.
	 * 
	 * @param intent
	 *            intent that triggered the task
	 * @param throwable
	 */
	protected void onTaskUnresolved(Intent intent, Throwable throwable) {
		Log.d("propoid-util", "unresolved task '" + intent.getAction() + "'",
				throwable);
	}

	/**
	 * Hook method for handling task failures.
	 * 
	 * @param task
	 * @param throwable
	 * @return should delayed tasks still be scheduled
	 * 
	 * @see Task#onExecute()
	 */
	protected boolean onTaskFailed(Task task, Throwable throwable) {
		Log.e("propoid-util", "failed task '" + throwable.getMessage() + "'",
				throwable);

		return false;
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

		if (constructor == null) {
			throw new IllegalArgumentException("no valid constructor");
		}

		return constructor;
	}

	/**
	 * An execution of a {@link Task}.
	 */
	class Execution implements Callable<Void>, Runnable {

		Task task;

		boolean publishing;

		/**
		 * Optional successive task.
		 * 
		 * @see #delay(Task)
		 */
		List<Task> delayed;

		public Execution(Task task) {
			this.task = task;

			task.execution = this;
		}

		/**
		 * Called by the {@link ExecutorService}.
		 */
		@Override
		public Void call() throws Exception {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

			try {
				task.onExecute();
			} catch (Throwable ex) {
				if (!onTaskFailed(task, ex)) {
					delayed = null;
				}
			}

			deschedule(this);

			if (delayed != null) {
				for (Task task : delayed) {
					schedule(task);
				}
			}

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

		public void delay(Task task) {
			if (delayed == null) {
				delayed = new ArrayList<Task>();
			}
			delayed.add(task);
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

		Execution execution;

		/**
		 * Execute the actual task.
		 */
		protected void onExecute() throws Exception {
		}

		/**
		 * Called by the service to allow this task to handle another task to be
		 * scheduled.
		 * <p>
		 * Overriden methods may
		 * <ul>
		 * <li>return {@code false} for unrelated tasks to be scheduled
		 * normally, thus running in parallel</li>
		 * <li>return {@code true} while dropping the task silently, e.g. if its
		 * purpose is already served by this task</li>
		 * <li>return {@code true} and delay the task to let it be scheduled
		 * after this task has finished</li>
		 * </ul>
		 * 
		 * @param other
		 * 
		 * @see #delay(Task)
		 */
		public boolean onScheduling(Task other) {
			return false;
		}

		/**
		 * Delay another task to be scheduled after this one.
		 * 
		 * @param other
		 *            task to delay
		 */
		public final void delay(Task other) {
			if (execution == null) {
				throw new IllegalStateException("not executing");
			}

			execution.delay(other);
		}

		/**
		 * Initiate publishing.
		 * 
		 * @see #onPublish()
		 */
		public final void publish() {
			if (execution == null) {
				throw new IllegalStateException("not executing");
			}

			execution.publish();
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
	 * Utility method to create an {@link Intent} for a service's {@link Task}.
	 * 
	 * @param context
	 *            context of intent
	 * @param task
	 *            action of intent
	 */
	public static <L extends TaskObserver> Intent createIntent(Context context,
			Class<? extends TaskService<L>.Task> action) {

		Intent intent = new Intent(context, action.getEnclosingClass());
		intent.setAction(action.getName());
		return intent;
	}
}