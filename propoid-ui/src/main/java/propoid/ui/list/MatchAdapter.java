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
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import propoid.core.Propoid;
import propoid.db.Match;
import propoid.db.Order;
import propoid.db.Range;
import propoid.db.aspect.Row;

/**
 * An adapter for {@link Match}.
 *
 * @see #restart(Activity)
 * @see #restart(Fragment)
 */
public abstract class MatchAdapter<T extends Propoid> extends GenericAdapter<T> {

	private final Match match;

	private Order[] ordering = new Order[0];

	private Range range = Range.all();

	protected MatchAdapter(Match match) {
		this(R.layout.simple_list_item_1, match);
	}

	protected MatchAdapter(int layoutId, Match match) {
		this(layoutId, R.layout.simple_dropdown_item_1line, match);
	}

	protected MatchAdapter(int layoutId, int dropDownLayoutId, Match match) {
		super(layoutId, dropDownLayoutId, (List)null);

		this.match = match;
	}

	public void setOrder(Order... ordering) {
		this.ordering = ordering;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public long getItemId(int position) {
		if (position < getCount()) {
			return Row.getID(getItem(position));
		}

		return Row.TRANSIENT;
	}

	@Override
	public List<T> getItems() {
		List<T> items = super.getItems();
		if (items == null) {
			items = load();
		}
		return items;
	}

	@Override
	public void setItems(List<T> items) {
		List<T> oldItems = super.getItems();
		if (oldItems != null) {
			// clear cursor for old items
			oldItems.clear();
		}

		super.setItems(items);
	}

	public void restart(int id, Activity activity) {
		restart(id, activity, activity.getLoaderManager());
	}

	public void restart(int id, Fragment fragment) {
		restart(id, fragment.getActivity(), fragment.getLoaderManager());
	}

	private void restart(int id, Context context, LoaderManager manager) {
		if (super.getItems() == null) {
			setItems(new ArrayList<T>());
		}

		manager.restartLoader(id, null, new Callbacks(context));
	}

	public void init(int id, Activity activity) {
		init(id, activity, activity.getLoaderManager());
	}

	public void init(int id, Fragment fragment) {
		init(id, fragment.getActivity(), fragment.getLoaderManager());
	}

	private void init(int id, Context context, LoaderManager manager) {
		if (super.getItems() == null) {
			setItems(new ArrayList<T>());
		}

		manager.initLoader(id, null, new Callbacks(context));
	}

	private List load() {
		return match.list(range, ordering);
	}

	private class Callbacks implements LoaderManager.LoaderCallbacks<List<T>> {

		private final Context context;

		Callbacks(Context context) {
			this.context = context;
		}

		@Override
		public Loader<List<T>> onCreateLoader(int id, Bundle args) {
			return new MatchLoader<T>(context, match, range, ordering);
		}

		@Override
		public void onLoadFinished(Loader<List<T>> loader, List<T> propoids) {
			setItems(propoids);
		}

		@Override
		public void onLoaderReset(Loader<List<T>> loader) {
			setItems(Collections.<T>emptyList());
		}
	}

	private static class MatchLoader<T extends Propoid> extends AsyncTaskLoader<List<T>> {

		private final Match<T> match;

		private final Range range;

		private final Order[] ordering;

		public MatchLoader(Context context, Match match, Range range, Order[] ordering) {
			super(context);

			this.match = match;
			this.range = range;
			this.ordering = ordering;
		}

		@Override
		protected void onStartLoading() {
			forceLoad();
		}

		@Override
		protected void onStopLoading() {
			cancelLoad();
		}

		@Override
		public List<T> loadInBackground() {
			return match.list(range, ordering);
		}
	}
}