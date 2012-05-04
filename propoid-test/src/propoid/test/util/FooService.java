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

	public class BarTask extends Task {
		private int current;
		private int max;

		public BarTask(Intent intent) {
			max = intent.getIntExtra("max", 10);
		}

		public boolean onScheduling(Task other) {
			if (other instanceof BazTask) {
				delay(other);
				return true;
			}

			return false;
		}

		protected void onExecute() {
			while (current < max) {
				current++;

				publish();
			}

			delay(new BazTask());
		}

		protected void onPublish(FooObserver observer) {
			observer.onBar(current);
		}
	}

	public class BazTask extends Task {
		protected void onExecute() {
			publish();
		}

		protected void onPublish(FooObserver observer) {
			observer.onBaz();
		}
	}

	public class UnresolvingTask extends Task {
		private UnresolvingTask() {
		}
	}

	public class XTask extends Task {
		protected void onExecute() {
			throw new IllegalStateException();
		}
	}
}