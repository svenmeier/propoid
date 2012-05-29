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
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * A binding of a boolean {@link Property} to a {@link CheckBox}.
 */
public class CheckBinding extends Binding<Boolean> implements
		OnCheckedChangeListener {

	/**
	 * Bind the property to the given view.
	 * 
	 * @param property
	 *            property to bind
	 * @param view
	 *            view to bind to
	 */
	public CheckBinding(Property<Boolean> property, View view) {
		super(property, view);

		onChange(property.get());

		getView().setOnCheckedChangeListener(this);
	}

	@Override
	protected void onChange(Boolean value) {
		getView().setChecked(Boolean.TRUE.equals(value));
	}

	@Override
	public CheckBox getView() {
		return (CheckBox) super.getView();
	}

	@Override
	protected void unbind() {
		super.unbind();

		getView().setOnCheckedChangeListener(null);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		property.set(isChecked);
	}
}
