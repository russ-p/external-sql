package com.github.russ_p.externalsql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SqlResolverTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSqlResolverString() throws Exception {
		SqlResolver resolver = new SqlResolver(
				SqlResolverTest.class.getClassLoader().getResource("test.sql").getFile());

		assertThat(resolver.getQueryNames()).hasSize(2);

		assertThat(resolver.getQuery("selectOne")).isNotEmpty();
		assertThat(resolver.getQuery("selectOne")).isEqualTo("select * from test_table;");

		assertThat(resolver.getQuery("selectTwo")).isNotEmpty();
		assertThat(resolver.getQuery("notExist")).isBlank();
	}

	@Test
	public void testSqlResolverClasspathString() throws Exception {
		SqlResolver resolver = new SqlResolver("classpath:test.sql");

		assertThat(resolver).isNotNull();
	}
}
