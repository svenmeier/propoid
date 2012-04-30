package propoid.test.util;

import propoid.util.service.TaskService;
import android.content.Intent;

public class FooService extends TaskService<FooObserver> {

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
}