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
 * @see #load(Activity)
 */
public abstract class MatchAdapter<T extends Propoid> extends GenericAdapter<T> {

	private final Match match;

	private Order[] ordering = new Order[0];

	private Range range = Range.all();

	protected MatchAdapter(Match match) {
		this(R.layout.simple_list_item_1, match);
	}

	protected MatchAdapter(int layoutId, Match match) {
		this(R.layout.simple_list_item_1, R.layout.simple_dropdown_item_1line, match);
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
		return Row.getID(getItem(position));
	}

	public void load(Activity activity) {
		activity.getLoaderManager().initLoader(1, null, new Loading(activity));
	}

	private class Loading extends AsyncTaskLoader<List<T>> implements android.app.LoaderManager.LoaderCallbacks<List<T>> {

		public Loading(Context context) {
			super(context);
		}

		@Override
		public Loader<List<T>> onCreateLoader(int id, Bundle args) {
			return this;
		}

		@Override
		public List<T> loadInBackground() {
			return match.list(range, ordering);
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
}