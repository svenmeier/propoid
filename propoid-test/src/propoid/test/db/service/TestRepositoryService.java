package propoid.test.db.service;

import propoid.db.Locator;
import propoid.db.locator.InMemoryLocator;
import propoid.db.service.RepositoryService;

/**
 */
public class TestRepositoryService extends RepositoryService {

	@Override
	protected Locator getLocator() {
		return new InMemoryLocator();
	}
}