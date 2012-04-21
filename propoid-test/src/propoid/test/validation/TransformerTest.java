package propoid.test.validation;

import junit.framework.TestCase;
import propoid.validation.WildcardValidator.Transformer;

public class TransformerTest extends TestCase {

	public void test() throws Exception {
		Transformer transformer = new Transformer();

		assertEquals("\\Qpropoid\\E", transformer.transform("propoid"));
		assertEquals("\\Qpro\\E.\\Qoid\\E", transformer.transform("pro?oid"));
		assertEquals("\\Qpro\\E.*\\Qoid\\E", transformer.transform("pro*oid"));
		assertEquals("\\Qpro.oid\\E", transformer.transform("pro.oid"));
		assertEquals("\\Qpro.\\E.*\\Qoid\\E", transformer.transform("pro.*oid"));
	}
}
