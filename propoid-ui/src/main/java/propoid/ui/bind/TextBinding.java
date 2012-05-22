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

import propoid.core.Property;
import propoid.ui.convert.Converter;
import propoid.ui.convert.ConverterException;
import propoid.ui.convert.NumberConverter;
import propoid.ui.convert.StringConverter;
import propoid.validation.ValidatorException;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A binding of a {@link Property} to a {@link TextView}. In case of an
 * {@link EditText} the binding is bidirectional.
 */
public class TextBinding<T> extends Binding<T> implements TextWatcher,
		OnKeyListener {

	private Converter<T> converter;

	/**
	 * Bind the property to the given view.
	 * 
	 * @param property
	 *            property to bind
	 * @param view
	 *            view to bind to
	 * @param converter
	 *            converter for values
	 */
	public TextBinding(Property<T> property, View view, Converter<T> converter) {
		super(property, view);

		if (getView() instanceof EditText) {
			getView().removeTextChangedListener(this);
			getView().setOnKeyListener(null);
		}

		this.converter = converter;

		onChange(property.get());
		safeChange(property.get());

		if (getView() instanceof EditText) {
			getView().addTextChangedListener(this);
			getView().setOnKeyListener(this);
		}
	}

	protected void onChange(T value) {
		getView().setText(converter.toString(value));
	}

	@Override
	protected void unbind() {
		super.unbind();

		if (getView() instanceof EditText) {
			getView().setOnKeyListener(null);
			getView().removeTextChangedListener(this);
		}
	}

	/**
	 * {@link KeyEvent#KEYCODE_BACK} on an empty {@link EditText} clears
	 * possible errors, thus we prevent this key code.
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DEL
				&& getView().getEditableText().toString().length() == 0) {
			return true;
		}

		return false;
	}

	@Override
	public void afterTextChanged(Editable editable) {
		String string = editable.toString();
		if ((getView().getInputType() & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0) {
			string = string.trim();
		}

		T value;

		try {
			value = converter.fromString(string);
		} catch (ConverterException ex) {
			getView().setError(ex.getMessage(getView().getContext()));
			return;
		}

		safeChange(value);
	}

	private void safeChange(T value) {
		try {
			change(value);

			getView().setError(null);
		} catch (ValidatorException ex) {
			getView().setError(ex.getMessage(getView().getContext()));
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public TextView getView() {
		return (TextView) super.getView();
	}

	/**
	 * Factory for binding of string {@link Property}s.
	 * 
	 * @param property
	 *            string property
	 * @param view
	 *            view to bind
	 * 
	 * @return binding
	 */
	public static TextBinding<String> string(Property<String> property,
			View view) {
		return new TextBinding<String>(property, view, new StringConverter());
	}

	/**
	 * Factory for binding of number {@link Property}s.
	 * 
	 * @param property
	 *            number property
	 * @param view
	 *            view to bind
	 * 
	 * @return binding
	 */
	public static <T extends Number> TextBinding<T> number(
			Property<T> property, View view, int resId) {

		if (view instanceof EditText) {
			TextView editText = (TextView) view;

			editText.setKeyListener(DigitsKeyListener
					.getInstance("0123456789-,."));
		}

		return new TextBinding<T>(property, view, new NumberConverter<T>(
				property, resId));
	}
}