package propoid.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import propoid.util.io.XmlNavigator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test for {@link XmlNavigator}.
 */
public class XmlNavigatorTest {

	private InputStream input;

	@Before
	public void setUp() {
		InputStream input = getClass()
				.getResourceAsStream(getClass().getSimpleName() + ".xml");
	}

	@After
	public void destroy() throws IOException {
		input.close();
		input = null;
	}

	@Test
	public void test() throws Exception {

		XmlNavigator navigator = new XmlNavigator(input);

		assertTrue(navigator.descent("level1"));

		assertTrue(navigator.descent("level2"));

		assertTrue(navigator.descent("level3"));
		assertEquals("value1", navigator.getAttributeValue("key"));
		navigator.ascent();

		assertTrue(navigator.descent("level3"));
		assertEquals("value2", navigator.getAttributeValue("key"));
		navigator.ascent();

		assertTrue(navigator.descent("level3"));
		assertEquals("value3", navigator.getAttributeValue("key"));
		navigator.ascent();

		assertFalse(navigator.descent("level3"));

		assertEquals("level2", navigator.getName());
		navigator.ascent();

		assertEquals("level1", navigator.getName());
		navigator.ascent();

		try {
			navigator.ascent();
			fail();
		} catch (IllegalStateException ex) {
		}
	}
}