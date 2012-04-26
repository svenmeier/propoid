package propoid.util.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
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

	private List<Execution> executions = new ArrayList<Execution>();

	private Executor executor;

	private Handler handler;

	protected TaskService() {
		this(new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>()));
	}

	protected TaskService(Executor executor) {
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
		if (executor instanceof ExecutorService) {
			((ExecutorService) executor).shutdown();
		}

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getAction() != null) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Task> action = (Class<? extends Task>) Class
						.forName(intent.getAction());

				resolveTask(action, intent);
			} catch (ClassNotFoundException ec) {
				Log.i("propoid-util", "invalid action '" + intent.getAction()
						+ "'");
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
	public void schedule(Task task) {
		schedule(new Execution(task));
	}

	private synchronized void schedule(Execution execution) {
		for (int w = 0; w < executions.size(); w++) {
			Execution candidate = executions.get(w);

			if (candidate.task.includes(execution.task)) {
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
	 * @param action
	 *            the requested task
	 * @param intent
	 *            the initiating intent
	 * 
	 * @see #schedule(Task)
	 */
	protected abstract void resolveTask(Class<? extends Task> action,
			Intent intent);

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
	 * The execution of a {@link Task}.
	 */
	private class Execution implements Runnable {

		public Task task;

		public Execution(Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

			try {
				task.onExecute();
			} catch (Exception ex) {
				onUnhandledException(ex);
			}

			deschedule(Execution.this);

			if (task.next != null) {
				schedule(task.next);
			}
		}
	}

	/**
	 * A task.
	 */
	public abstract class Task {

		/**
		 * Optional next task.
		 * 
		 * @see #append(Task)
		 */
		Task next;

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
		public synchronized final void append(Task other) {
			if (next == null) {
				next = other;
			} else {
				next.append(other);
			}
		}

		protected void onExecute() {
		}

		/**
		 * Initiate publishing.
		 * 
		 * @see #onPublish()
		 */
		public final void publish() {
			Publisher publisher = new Publisher();

			handler.post(publisher);

			synchronized (publisher) {
				while (!publisher.done) {
					try {
						publisher.wait();
					} catch (InterruptedException interrupted) {
					}
				}
			}
		}

		private class Publisher implements Runnable {
			public boolean done = false;

			@Override
			public synchronized void run() {
				onPublish();

				done = true;

				notifyAll();
			}
		}

		/**
		 * Publish delegates to {@link #onPublish(TaskObserver)} for each
		 * subscribed observer.
		 */
		public void onPublish() {
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
		public void onPublish(L observer) {
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