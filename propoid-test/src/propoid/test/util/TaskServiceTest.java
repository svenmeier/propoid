package propoid.test.util;

import propoid.test.util.FooService.BarTask;
import propoid.test.util.FooService.FailingTask;
import propoid.test.util.FooService.UnresolvingTask;
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

		synchronized (observer) {
			observer.wait();
		}

		assertTrue(observer.onBarCalled);
	}

	public void testFailed() throws Exception {
		getInstrumentation().getContext().startService(
				FooService.createIntent(getInstrumentation().getContext(),
						FailingTask.class));

		assertEquals(IllegalStateException.class, FooService.queue.take()
				.getClass());
	}

	public void testUnresolved() throws Exception {
		getInstrumentation().getContext().startService(
				FooService.createIntent(getInstrumentation().getContext(),
						UnresolvingTask.class));

		assertEquals(IllegalAccessException.class, FooService.queue.take()
				.getClass());
	}

	private class TestFooObserver extends FooObserver {
		public boolean onBarCalled;

		@Override
		public void onBar(int current) {
			assertSame(Looper.getMainLooper().getThread(),
					Thread.currentThread());

			onBarCalled = true;

			synchronized (this) {
				notifyAll();
			}
		}
	}
}