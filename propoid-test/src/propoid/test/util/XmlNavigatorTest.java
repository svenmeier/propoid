package propoid.test.util;

import junit.framework.TestCase;
import propoid.util.io.XmlNavigator;

/**
 * Test for {@link XmlNavigator}.
 */
public class XmlNavigatorTest extends TestCase {

	public void test() throws Exception {

		XmlNavigator navigator = new XmlNavigator(getClass()
				.getResourceAsStream(getClass().getSimpleName() + ".xml"));

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