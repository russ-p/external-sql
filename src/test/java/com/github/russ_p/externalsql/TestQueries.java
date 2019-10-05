package com.github.russ_p.externalsql;

@ExternalSQL("classpath:test.sql")
public interface TestQueries {

	String selectOne();

	String selectTwo();
}
