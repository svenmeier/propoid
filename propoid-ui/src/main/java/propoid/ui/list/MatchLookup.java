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

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import java.util.Collections;
import java.util.List;

import propoid.core.Propoid;
import propoid.db.Match;
import propoid.db.Order;
import propoid.db.Range;

/**
 */
public abstract class MatchLookup<T extends Propoid> {

	private final Match<T> match;

	private Order[] ordering = new Order[0];

	private Range range = Range.all();

	protected MatchLookup(Match<T> match) {
		this.match = match;
	}

	public Match<T> getMatch() {
		return match;
	}

	public void setOrder(Order... ordering) {
		this.ordering = ordering;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	/**
	 * Force restart of an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param activity context
	 */
	public void restartLoader(int id, FragmentActivity activity) {
		restartLoader(id, activity, LoaderManager.getInstance(activity));
	}

	/**
	 * Force restart of an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param fragment context
	 */
	public void restartLoader(int id, Fragment fragment) {
		restartLoader(id, fragment.getActivity(), LoaderManager.getInstance(fragment));
	}

	private void restartLoader(int id, Context context, LoaderManager manager) {
		manager.restartLoader(id, null, new Callbacks(context));
	}

	/**
	 * Initialize an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param activity context
	 */
	public void initLoader(int id, FragmentActivity activity) {
		initLoader(id, activity, activity.getSupportLoaderManager());
	}

	/**
	 * Initialize an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param fragment context
	 */
	public void initLoader(int id, Fragment fragment) {
		initLoader(id, fragment.getActivity(), fragment.getLoaderManager());
	}

	private void initLoader(int id, Context context, LoaderManager manager) {
		manager.initLoader(id, null, new Callbacks(context));
	}

	/**
	 * Destroy asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param activity context
	 */
	public void destroy(int id, FragmentActivity activity) {
		destroy(id, activity, activity.getSupportLoaderManager());
	}

	/**
	 * Destroy asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param fragment context
	 */
	public void destroy(int id, Fragment fragment) {
		destroy(id, fragment.getActivity(), fragment.getLoaderManager());
	}

	private void destroy(int id, Context context, LoaderManager manager) {
		manager.destroyLoader(id);
	}

	private class Callbacks implements LoaderManager.LoaderCallbacks<List<T>> {

		private final Context context;

		Callbacks(Context context) {
			this.context = context;
		}

		@Override
		public Loader<List<T>> onCreateLoader(int id, Bundle args) {
			return new MatchLoader<T>(context, getMatch(), range, ordering);
		}

		@Override
		public void onLoadFinished(Loader<List<T>> loader, List<T> propoids) {
			onLookup(propoids);
		}

		@Override
		public void onLoaderReset(Loader<List<T>> loader) {
			onLookup(Collections.<T>emptyList());
		}
	}

	protected abstract void onLookup(List<T> propoids);

	private static class MatchLoader<T extends Propoid> extends AsyncTaskLoader<List<T>> {

		private final Match<T> match;

		private final Range range;

		private final Order[] ordering;

		private ContentObserver observer = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				forceLoad();
			}
		};

		public MatchLoader(Context context, Match match, Range range, Order[] ordering) {
			super(context);

			this.match = match;
			this.range = range;
			this.ordering = ordering;

			context.getContentResolver().registerContentObserver(match.getUri(), true, observer);
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
			List<T> list = match.list(range, ordering);

			// Ensure the cursor window is filled.
			list.size();

			return list;
		}

		@Override
		protected void onReset() {
			getContext().getContentResolver().unregisterContentObserver(observer);
		}
	}
}