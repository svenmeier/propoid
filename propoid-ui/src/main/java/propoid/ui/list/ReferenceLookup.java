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

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;

import propoid.core.Propoid;
import propoid.db.LookupException;
import propoid.db.Reference;
import propoid.db.Repository;

/**
 * A lookup of a reference. To get the result in {@link #onLookup(Propoid)}, one of the
 * {@code initLoader()} or {@code restartLoader()} methods has to be called.
 *
 * @see #initLoader(int, Activity)
 * @see #initLoader(int, Fragment)
 * @see #onLookup(Propoid)
 *
 * @param <T>
 */
public abstract class ReferenceLookup<T extends Propoid>{

	private Reference<T> reference;

	private Repository repository;

	protected ReferenceLookup(Repository repository, Reference<T> reference) {
		this.repository = repository;
		this.reference = reference;
	}

	/**
	 * Hook method called when the propoid was looked up.
	 *
	 * @param propoid	looked up propoid or {@code null}
	 */
	protected abstract void onLookup(T propoid);

	/**
	 * Force restart of an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param activity context
	 */
	public void restartLoader(int id, Activity activity) {
		restartLoader(id, activity, activity.getLoaderManager());
	}

	/**
	 * Force restart of an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param fragment context
	 */
	public void restartLoader(int id, Fragment fragment) {
		restartLoader(id, fragment.getActivity(), fragment.getLoaderManager());
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
	public void initLoader(int id, Activity activity) {
		initLoader(id, activity, activity.getLoaderManager());
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
	public void destroy(int id, Activity activity) {
		destroy(id, activity, activity.getLoaderManager());
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

	private class Callbacks implements LoaderManager.LoaderCallbacks<T> {

		private final Context context;

		Callbacks(Context context) {
			this.context = context;
		}

		@Override
		public Loader<T> onCreateLoader(int id, Bundle args) {
			return new ReferenceLoader<T>(context, repository, reference);
		}

		@Override
		public void onLoadFinished(Loader<T> loader, T propoid) {
			onLookup(propoid);
		}

		@Override
		public void onLoaderReset(Loader<T> loader) {
			onLookup(null);
		}
	}

	private static class ReferenceLoader<T extends Propoid> extends AsyncTaskLoader<T> {

		private final Repository repository;

		private final Reference<T> reference;

		private ContentObserver observer = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				forceLoad();
			}
		};

		public ReferenceLoader(Context context, Repository repository, Reference<T> reference) {
			super(context);

			this.repository = repository;
			this.reference = reference;

			context.getContentResolver().registerContentObserver(reference.toUri(), true, observer);
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
		public T loadInBackground() {
			try {
				return repository.lookup(reference);
			} catch (LookupException ex) {
				return null;
			}
		}

		@Override
		protected void onReset() {
			getContext().getContentResolver().unregisterContentObserver(observer);
		}
	}
}