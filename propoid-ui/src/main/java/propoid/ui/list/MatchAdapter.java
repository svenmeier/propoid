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
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

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
 * @see #load(FragmentActivity)
 * @see #load(Fragment)
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
		super(layoutId, dropDownLayoutId);

		this.match = match;

		setItems(new ArrayList<T>());
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

	public void load(FragmentActivity activity) {
		Loading loading = new Loading(activity, this);

		activity.getSupportLoaderManager().restartLoader(loading.id, null, loading);
	}

	public void load(Fragment fragment) {
		Loading loading = new Loading(fragment.getActivity(), this);

		fragment.getLoaderManager().restartLoader(loading.id, null, loading);
	}

	private static class Loading<T> extends AsyncTaskLoader<List<T>> implements LoaderManager.LoaderCallbacks<List<T>> {

		private static int counter;

		private final MatchAdapter adapter;

		private final int id;

		public Loading(Context context, MatchAdapter adapter) {
			super(context);

			this.adapter = adapter;

			this.id = counter++;
		}

		@Override
		public Loader<List<T>> onCreateLoader(int id, Bundle args) {
			return this;
		}

		@Override
		public void onLoadFinished(Loader<List<T>> loader, List<T> propoids) {
			adapter.setItems(propoids);
		}

		@Override
		public void onLoaderReset(Loader<List<T>> loader) {
			adapter.setItems(Collections.<T>emptyList());
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
			return adapter.match.list(adapter.range, adapter.ordering);
		}
	}
}