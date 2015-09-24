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
package propoid.ui.bind;

import java.util.Arrays;
import java.util.List;

import propoid.core.Property;
import propoid.ui.convert.Converter;
import propoid.ui.convert.EnumConverter;
import propoid.ui.convert.NumberConverter;
import propoid.ui.convert.StringConverter;
import propoid.ui.list.GenericAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * A binding of a {@link Property} to a {@link Spinner}.
 */
public class SpinnerBinding<T> extends Binding<T> implements
		OnItemSelectedListener {

	private SpinnerAdapter adapter;

	/**
	 * Bind the property to the given view.
	 * 
	 * @param property
	 *            property to bind
	 * @param view
	 *            view to bind to
	 * @param ts
	 *            values to choose from
	 */
	public SpinnerBinding(Property<T> property, View view,
			Converter<T> converter, T... ts) {
		this(property, view, converter, Arrays.asList(ts));
	}

	/**
	 * Bind the property to the given view.
	 * 
	 * @param property
	 *            property to bind
	 * @param view
	 *            view to bind to
	 * @param ts
	 *            values to choose from
	 */
	public SpinnerBinding(final Property<T> property, View view,
			final Converter<T> converter, List<T> ts) {
		this(property, view, new GenericAdapter<T>(
				android.R.layout.simple_spinner_item,
				android.R.layout.simple_spinner_dropdown_item, ts) {
			@Override
			protected void bind(int position, View view, T item) {
				((TextView) view).setText(converter.toString(item));
			}
		});
	}

	/**
	 * Bind the property to the given view.
	 * 
	 * @param property
	 *            property to bind
	 * @param view
	 *            view to bind to
	 * @param adapter
	 *            values to choose from
	 */
	public SpinnerBinding(Property<T> property, View view,
			SpinnerAdapter adapter) {
		super(property, view);

		this.adapter = adapter;

		getView().setAdapter(adapter);

		onChange(property.get());

		getView().setOnItemSelectedListener(this);
	}

	protected void onChange(T value) {
		for (int i = 0; i < adapter.getCount(); i++) {
			Object item = adapter.getItem(i);
			if ((value == null && item == null)
					|| (item != null && item.equals(value))) {
				getView().setSelection(i);
				break;
			}
		}
	}

	@Override
	protected void unbind() {
		super.unbind();

		getView().setOnItemSelectedListener(null);
	}

	@Override
	public Spinner getView() {
		return (Spinner) super.getView();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int index, long id) {
		change((T) adapterView.getSelectedItem());
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
	}

	/**
	 * Convenience factory for binding of enumeration {@link Property}s.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> SpinnerBinding<E> enumeration(
			Property<E> property, Spinner view) {
		E[] ts = ((Class<E>) property.meta().type).getEnumConstants();

		return new SpinnerBinding<E>(property, view, new EnumConverter<E>(
				property), ts);
	}

	/**
	 * Convenience factory for binding of string {@link Property}s.
	 */
	public static SpinnerBinding<String> string(Property<String> property,
			View view, List<String> strings) {
		return new SpinnerBinding<String>(property, view,
				new StringConverter(), strings);
	}

	/**
	 * Convenience factory for binding of string {@link Property}s.
	 */
	public static SpinnerBinding<String> string(Property<String> property,
			View view, String... strings) {
		return new SpinnerBinding<String>(property, view,
				new StringConverter(), strings);
	}

	/**
	 * Convenience factory for binding of number {@link Property}s.
	 */
	public static <T extends Number> SpinnerBinding<T> number(
			Property<T> property, View view, int resId, T... ts) {
		return new SpinnerBinding<T>(property, view, new NumberConverter<T>(
				property, resId), Arrays.asList(ts));
	}

	/**
	 * Convenience factory for binding of number {@link Property}s.
	 */
	public static <T extends Number> SpinnerBinding<T> number(
			Property<T> property, View view, int resId, List<T> ts) {
		return new SpinnerBinding<T>(property, view, new NumberConverter<T>(
				property, resId), ts);
	}
}