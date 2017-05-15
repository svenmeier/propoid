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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A generified adapter which can be used for {@link RecyclerView}s.
 */
public abstract class GenericRecyclerAdapter<T> extends RecyclerView.Adapter<GenericRecyclerAdapter.GenericHolder> {

	private final int layoutId;

	protected List<T> items;

	public GenericRecyclerAdapter(int layoutId, List<T> items) {

		this.layoutId  = layoutId;
		this.items = items;
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	@Override
	public GenericHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View v = (View) LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

		return createHolder(v);
	}

	@Override
	public void onBindViewHolder(GenericHolder holder, int position) {
		holder.bind(items.get(position));
	}

	protected abstract GenericHolder createHolder(View v);

	public static abstract class GenericHolder<T> extends RecyclerView.ViewHolder {

		protected T item;

		public GenericHolder(View view) {
			super(view);
		}

		public final void bind(T t) {
			this.item = t;

			onBind();
		}

		protected abstract void onBind();
	}
}