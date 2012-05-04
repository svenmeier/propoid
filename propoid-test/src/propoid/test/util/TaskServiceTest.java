package propoid.test.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import propoid.test.util.FooService.BarTask;
import propoid.test.util.FooService.BazTask;
import propoid.test.util.FooService.UnresolvingTask;
import propoid.test.util.FooService.XTask;
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

		getInstrumentation().getContext().startService(
				FooService.createIntent(getInstrumentation().getContext(),
						BazTask.class));

		assertEquals("bar:1", observer.queue.take());
		assertEquals("bar:2", observer.queue.take());
		assertEquals("bar:3", observer.queue.take());
		assertEquals("bar:4", observer.queue.take());
		assertEquals("bar:5", observer.queue.take());
		assertEquals("bar:6", observer.queue.take());
		assertEquals("bar:7", observer.queue.take());
		assertEquals("bar:8", observer.queue.take());
		assertEquals("bar:9", observer.queue.take());
		assertEquals("bar:10", observer.queue.take());
		assertEquals("baz", observer.queue.take());
		assertEquals("baz", observer.queue.take());
	}

	public void testFailed() throws Exception {
		getInstrumentation().getContext().startService(
				FooService.createIntent(getInstrumentation().getContext(),
						XTask.class));

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

		public final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(
				10);

		@Override
		public void onBar(int current) {
			assertSame(Looper.getMainLooper().getThread(),
					Thread.currentThread());

			queue.add("bar:" + current);
		}

		@Override
		public void onBaz() {
			assertSame(Looper.getMainLooper().getThread(),
					Thread.currentThread());

			queue.add("baz");
		}
	}
}