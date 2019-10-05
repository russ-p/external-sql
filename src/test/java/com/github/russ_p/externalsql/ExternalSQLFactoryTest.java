package com.github.russ_p.externalsql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ExternalSQLFactoryTest {

	private ExternalSQLFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new ExternalSQLFactory();
	}

	@Test
	public void testCreate() throws Exception {
		TestQueries testQueries = factory.create(TestQueries.class);

		assertThat(testQueries.selectOne()).isNotEmpty();
		assertThat(testQueries.selectOne()).isEqualTo("select * from test_table;");
		assertThat(testQueries.selectTwo()).isNotEmpty();
	}

}
