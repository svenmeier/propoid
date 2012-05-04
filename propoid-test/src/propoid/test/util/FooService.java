package propoid.test.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import propoid.util.service.TaskService;
import android.content.Intent;

public class FooService extends TaskService<FooObserver> {

	public static final BlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(
			1);

	@Override
	protected void onTaskUnresolved(Intent intent, Throwable ex) {
		queue.add(ex);
	}

	protected boolean onTaskFailed(Task task, Throwable ex) {
		queue.add(ex);

		return false;
	}

	// must be public
	public class BarTask extends Task {
		private int current;
		private int max;

		// must be public, argument is optional
		public BarTask(Intent intent) {
			max = intent.getIntExtra("max", 10);
		}

		// asynchronously executed
		protected void onExecute() {
			while (current < max) {
				// simulate expensive calculation
				try {
					Thread.sleep(1000);
				} catch (InterruptedException interrupted) {
				}

				current++;

				// initiate publish
				publish();
			}
		}

		// publish on main thread
		protected void onPublish(FooObserver observer) {
			observer.onBar(current);
		}
	}

	public class UnresolvingTask extends Task {
		private UnresolvingTask() {
		}
	}

	public class FailingTask extends Task {
		protected void onExecute() {
			throw new IllegalStateException();
		}
	}
}