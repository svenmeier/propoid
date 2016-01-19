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
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import propoid.core.Propoid;
import propoid.db.Match;
import propoid.db.Order;
import propoid.db.Range;
import propoid.db.aspect.Row;

/**
 * An adapter for {@link Match}. To show the actual result, one of the {@code initLoader()} or
 * {@code restartLoader()} methods has to be called.
 *
 * @see #initLoader(int, Activity)
 * @see #initLoader(int, Fragment)
 */
public abstract class MatchAdapter<T extends Propoid> extends GenericAdapter<T> {

	private final MatchLookup lookup;

	protected MatchAdapter(Match<T> match) {
		this(R.layout.simple_list_item_1, match);
	}

	protected MatchAdapter(int layoutId, Match<T> match) {
		this(layoutId, R.layout.simple_dropdown_item_1line, match);
	}

	protected MatchAdapter(int layoutId, int dropDownLayoutId, Match<T> match) {
		super(layoutId, dropDownLayoutId, new ArrayList<T>());

		this.lookup = new MatchLookup<T>(match) {
			@Override
			protected void onLookup(List<T> propoids) {
				setItems(propoids);
			}
		};
	}

	public void setOrder(Order... ordering) {
		lookup.setOrder(ordering);
	}

	public void setRange(Range range) {
		lookup.setRange(range);
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
	public void setItems(List<T> items) {
		List<T> oldItems = super.getItems();
		if (oldItems != null) {
			// clear cursor for old items
			oldItems.clear();
		}

		super.setItems(items);
	}

	/**
	 * Force restart of an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param activity context
	 */
	public void restartLoader(int id, Activity activity) {
		lookup.restartLoader(id, activity);
	}

	/**
	 * Force restart of an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param fragment context
	 */
	public void restartLoader(int id, Fragment fragment) {
		lookup.restartLoader(id, fragment);
	}

	/**
	 * Initialize an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param activity context
	 */
	public void initLoader(int id, Activity activity) {
		lookup.initLoader(id, activity);
	}

	/**
	 * Initialize an asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param fragment context
	 */
	public void initLoader(int id, Fragment fragment) {
		lookup.initLoader(id, fragment);
	}

	/**
	 * Destroy asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param activity context
	 */
	public void destroy(int id, Activity activity) {
		lookup.destroy(id, activity);
	}

	/**
	 * Destroy asynchronous loader.
	 *
	 * @param id uniqure id
	 * @param fragment context
	 */
	public void destroy(int id, Fragment fragment) {
		lookup.destroy(id, fragment);
	}
}