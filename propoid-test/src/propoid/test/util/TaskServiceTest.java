package propoid.test.util;

import propoid.test.util.FooService.BarTask;
import propoid.util.service.TaskService;
import android.os.Looper;
import android.test.InstrumentationTestCase;

/**
 * Test for {@link TaskService}.
 */
public class TaskServiceTest extends InstrumentationTestCase {

	public void test() throws Exception {
		TestFooObserver observer = new TestFooObserver();

		observer.subscribe(getInstrumentation().getContext(), FooService.class);

		getInstrumentation().getContext().startService(
				FooService.createIntent(getInstrumentation().getContext(),
						BarTask.class));

		Thread.sleep(2000);

		assertTrue(observer.onBarCalled);
	}

	private class TestFooObserver extends FooObserver {
		public boolean onBarCalled;

		@Override
		public void onBar(int current) {
			assertSame(Looper.getMainLooper().getThread(),
					Thread.currentThread());

			onBarCalled = true;
		}
	}
}