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
package propoid.ui.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

/**
 * A wrapper of an {@link AsyncTask}, executing the task on a click.
 * 
 * @see android.view.View.OnClickListener#onClick(View)
 * @see android.content.DialogInterface.OnClickListener#onClick(DialogInterface,
 *      int)
 */
public class Executor<P> implements
		android.content.DialogInterface.OnClickListener,
		android.view.View.OnClickListener {

	private AsyncTask<P, ?, ?> task;

	private P[] params;

	private Executor(AsyncTask<P, ?, ?> task, P... params) {
		this.task = task;
		this.params = params;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == AlertDialog.BUTTON_POSITIVE) {
			task.execute(params);
		}
	}

	@Override
	public void onClick(View view) {
		task.execute(params);
	}

	/**
	 * Execute the given task with the given parameters on click.
	 */
	public static <P> Executor<P> onClick(AsyncTask<P, ?, ?> task, P... params) {
		return new Executor<P>(task, params);
	}
}