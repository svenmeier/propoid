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
package propoid.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * A simple navigator on an XML tree.
 */
public class XmlNavigator {

	private XmlPullParser parser;

	private LinkedList<Descent> stack = new LinkedList<Descent>();

	public XmlNavigator(InputStream input) throws IOException,
			XmlPullParserException {
		this(input, null);
	}

	public XmlNavigator(Reader reader) throws IOException,
			XmlPullParserException {
		parser = Xml.newPullParser();

		try {
			parser.setInput(reader);
		} catch (XmlPullParserException ex) {
			IOException ioException = new IOException();
			ioException.initCause(ex);
			throw ioException;
		}

		stack.add(new Descent(0, null));
	}

	public XmlNavigator(InputStream input, String encoding) throws IOException {

		parser = Xml.newPullParser();

		try {
			parser.setInput(input, encoding);
		} catch (XmlPullParserException ex) {
			IOException ioException = new IOException();
			ioException.initCause(ex);
			throw ioException;
		}

		stack.add(new Descent(0, null));
	}

	/**
	 * Descent the XML tree to the descendent with the given name
	 * 
	 * @param name
	 *            name of descendent to descent to
	 * @return {@code true} id descendent was found
	 */
	public boolean descent(String name) throws IOException {
		if (stack.getLast().consumed) {
			return false;
		}

		int depth = 0;

		while (true) {
			int event = next();

			if (event == XmlPullParser.START_TAG) {
				depth++;

				if (name.equals(parser.getName())) {
					stack.add(new Descent(depth, parser.getName()));
					return true;
				}
			} else if (event == XmlPullParser.END_TAG
					|| event == XmlPullParser.END_DOCUMENT) {
				if (depth == 0) {
					stack.getLast().consumed = true;
					return false;
				}

				depth--;
			}
		}
	}

	/**
	 * Ascent back from a previous {@link #descent(String)}.
	 */
	public void ascent() throws IOException {
		if (stack.size() == 1) {
			throw new IllegalStateException();
		}

		Descent descent = stack.removeLast();

		if (descent.consumed) {
			return;
		}

		int depth = descent.depth;

		while (true) {
			int event = next();
			if (event == XmlPullParser.START_TAG) {
				depth++;
			} else if (event == XmlPullParser.END_TAG) {
				depth--;

				if (depth == 0) {
					return;
				}
			}
		}
	}

	/**
	 * Get the name at the current descent.
	 */
	public String getName() {
		return stack.getLast().name;
	}

	/**
	 * Get the attribute value for the given name at the current descent.
	 */
	public String getAttributeValue(String name) {
		return parser.getAttributeValue(null, name);
	}

	/**
	 * Get the text of the current node.
	 */
	public String getText() throws IOException {
		if (next() != XmlPullParser.TEXT) {
			throw new IOException("text expected");
		}

		return parser.getText();
	}

	/**
	 * Convenience method to descent to the node with the given name, get its
	 * text and ascent again.
	 * 
	 * @param name
	 *            name of node to descent to
	 * @return text
	 * @throws IOException
	 *             if not present
	 */
	public String getText(String name) throws IOException {
		if (!descent(name)) {
			throw new IOException("tag expected '" + name + "'");
		}

		String text = getText();

		ascent();

		return text;
	}

	private int next() throws IOException {
		try {
			return parser.next();
		} catch (XmlPullParserException ex) {
			IOException ioException = new IOException();
			ioException.initCause(ex);
			throw ioException;
		}
	}

	private class Descent {
		public final int depth;

		public final String name;

		public boolean consumed;

		public Descent(int depth, String name) {
			this.depth = depth;
			this.name = name;
		}
	}
}
