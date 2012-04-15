package propoid.test.ui;

import junit.framework.TestCase;
import propoid.ui.list.MergeAdapter;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class MergeAdapterTest extends TestCase {

	public void test() throws Exception {
		ListAdapter a = new TestListAdapter(1);
		ListAdapter b = new TestListAdapter(2);
		ListAdapter c = new TestListAdapter(0, 2);
		ListAdapter d = new TestListAdapter(4);

		MergeAdapter adapter = new MergeAdapter(a, b, c, d);

		assertEquals(9, adapter.getCount());
		assertEquals(7, adapter.getViewTypeCount());

		assertEquals(0, adapter.getItemViewType(0));
		assertEquals(1, adapter.getItemViewType(1));
		assertEquals(2, adapter.getItemViewType(2));
		assertEquals(-1, adapter.getItemViewType(3));
		assertEquals(-1, adapter.getItemViewType(4));
		assertEquals(3, adapter.getItemViewType(5));
		assertEquals(4, adapter.getItemViewType(6));
		assertEquals(5, adapter.getItemViewType(7));
		assertEquals(6, adapter.getItemViewType(8));
	}

	public class TestListAdapter implements ListAdapter {

		private int types;
		private int count;

		public TestListAdapter(int types) {
			this(types, types);
		}

		public TestListAdapter(int types, int count) {
			this.types = types;
			this.count = count;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public int getItemViewType(int position) {
			if (types == 0) {
				return IGNORE_ITEM_VIEW_TYPE;
			} else {
				return position;
			}
		}

		@Override
		public int getViewTypeCount() {
			return types;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getItem(int position) {
			throw new UnsupportedOperationException();
		}

		@Override
		public long getItemId(int position) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasStableIds() {
			throw new UnsupportedOperationException();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean areAllItemsEnabled() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEnabled(int position) {
			throw new UnsupportedOperationException();
		}
	}
}
