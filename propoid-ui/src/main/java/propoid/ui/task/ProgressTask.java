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

import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * An asynchronous task showing a progress dialog.
 */
public abstract class ProgressTask<P, U, R> extends AsyncTask<P, U, R> {

	private ProgressDialog progress;

	private Exception exception;

	@Override
	protected final void onPreExecute() {
		progress = onStarting();
	}

	protected abstract ProgressDialog onStarting();

	protected final R doInBackground(P... params) {
		try {
			return onExecute(params);
		} catch (Exception ex) {
			exception = ex;
			return null;
		}
	}

	@Override
	protected final void onCancelled() {
		if (progress != null) {
			try {
				progress.dismiss();
			} catch (Exception viewNotAttached) {
			}
		}
		progress = null;

		onCancel();
	}

	protected abstract R onExecute(P... params) throws Exception;

	protected final void onProgressUpdate(U... values) {
		onProgress(progress, values);
	}

	protected void onProgress(ProgressDialog progress, U... values) {
	}

	@Override
	protected final void onPostExecute(R result) {

		try {
			progress.dismiss();
		} catch (Exception viewNotAttached) {
		}

		if (exception == null) {
			onSuccess(result);
		} else {
			onFailure(exception);
		}
	}

	protected void onCancel() {
	}

	protected void onSuccess(R result) {
	}

	protected void onFailure(Exception ex) {
	}
}