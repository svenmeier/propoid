/*
 * Copyright 2011 Sven Meier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package propoid.ui.list;

import android.R;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A generified adapter which can be used for {@link ListView}s.
 * <p/>
 * For convenience this adapter implements
 * {@link #onItemClick(AdapterView, View, int, long)} delegating to
 * {@link #onItem(Object, int)}.
 */
public abstract class GenericListAdapter<T> implements ListAdapter, SpinnerAdapter,
		OnItemClickListener {

	private final ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();

	private int layoutId;

	private int dropDownLayoutId;

	private List<T> items;

	private LayoutInflater inflater;

	protected GenericListAdapter(T... items) {
		this(Arrays.asList(items));
	}

	protected GenericListAdapter(List<T> items) {
		this(R.layout.simple_list_item_1, items);
	}

	protected GenericListAdapter(int layoutId, T... items) {
		this(layoutId, Arrays.asList(items));
	}

	protected GenericListAdapter(int layoutId, List<T> items) {
		this(layoutId, R.layout.simple_dropdown_item_1line, items);
	}

	protected GenericListAdapter(int layoutId, int dropDownLayoutId, T... items) {
		this(layoutId, dropDownLayoutId, Arrays.asList(items));
	}

	protected GenericListAdapter(int layoutId, int dropDownLayoutId, List<T> items) {
		this.layoutId = layoutId;
		this.dropDownLayoutId = dropDownLayoutId;

		this.items = items;
	}

	public void setItems(List<T> items) {
		this.items = items;

		notifyChanged();
	}

	public void notifyChanged() {
		for (DataSetObserver observer : observers) {
			observer.onChanged();
		}
	}

	/**
	 * Get the items of this adapter.
	 *
	 * @return items
	 */
	public List<T> getItems() {
		return items;
	}

	/**
	 * @param observer
	 */
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (!observers.remove(observer)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		observers.add(observer);
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return getItems().isEmpty();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public T getItem(int position) {
		return getItems().get(position);
	}

	@Override
	public int getCount() {
		return getItems().size();
	}

	public int position(T t) {
		return getItems().indexOf(t);
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		return getViewImpl(position, convertView, parent, layoutId);
	}

	private View getViewImpl(int position, View view, ViewGroup parent,
							 int layoutId) {
		T item = getItems().get(position);

		if (view == null) {
			if (inflater == null) {
				inflater = LayoutInflater.from(parent.getContext());
			}

			view = inflater.inflate(layoutId, parent, false);

			init(view);
		}

		bind(position, view, item);

		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getViewImpl(position, convertView, parent, dropDownLayoutId);
	}

	/**
	 * Handle a click on a {@link ListView}.
	 *
	 * @see ListView#setOnItemClickListener(OnItemClickListener)
	 */
	@Override
	public final void onItemClick(AdapterView<?> arg0, View arg1, int item,
								  long arg3) {
		onItem(getItem(item), item);
	}

	/**
	 * Hook method for clicks on the given item.
	 *
	 * @param item     clicked item
	 * @param position
	 */
	protected void onItem(T item, int position) {
	}

	/**
	 * Init a view once on creation.
	 */
	protected void init(View view) {
	}

	/**
	 * Bind the given item for the given view.
	 *
	 * @param position position of view in list
	 * @param view     view to bind to
	 * @param item     item to bind
	 */
	protected abstract void bind(int position, View view, T item);

	/**
	 * Helper method to install to the given {@link ListView}, i.e. set as
	 * adapter and register as {@link OnItemClickListener}.
	 */
	public GenericListAdapter<T> install(ListView view) {
		view.setAdapter(this);
		view.setOnItemClickListener(this);

		return this;
	}
}