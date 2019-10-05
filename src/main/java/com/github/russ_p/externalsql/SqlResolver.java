package com.github.russ_p.externalsql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

class SqlResolver {

	private static final String NAME_COMMENT_PREFIX = "-- @";
	private static final String COMMENT_PREFIX = "--";

	private final Map<String, String> queries = new HashMap<>();

	public SqlResolver(String filename) throws IOException {
		this(new File(resolvePath(filename)));
	}

	public SqlResolver(File file) throws IOException {
		parseFile(file);
	}

	public Set<String> getQueryNames() {
		return queries.keySet();
	}

	public String getQuery(String name) {
		return queries.getOrDefault(name, "");
	}

	private void put(String name, String query) {
		if (name == null || name.isEmpty())
			return;
		if (query == null || query.isEmpty())
			return;

		queries.put(name, query);
	}

	private void parseFile(File file) throws IOException {
		String queryName = "";
		StringBuffer query = new StringBuffer();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			while (line != null) {
				if (isSqlHeaderComment(line)) {
					put(queryName, query.toString().trim());
					queryName = getName(line);
					query = new StringBuffer();
				} else if (isSimpleComment(line)) {

				} else {
					query.append(line).append("\n");
				}

				// read next line
				line = reader.readLine();
			}
		}
		put(queryName, query.toString());
	}

	private boolean isSimpleComment(String line) {
		return line.startsWith(COMMENT_PREFIX) && !isSqlHeaderComment(line);
	}

	private boolean isSqlHeaderComment(String line) {
		return line.startsWith(NAME_COMMENT_PREFIX);
	}

	private String getName(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		while (tokenizer.hasMoreElements()) {
			String str = tokenizer.nextToken();
			if (str.startsWith("@")) {
				return str.substring(1);
			}

		}
		throw new IllegalStateException("No sql query name found in line " + line);
	}

	private static String resolvePath(String path) {
		if (path.startsWith("classpath:")) {
			return SqlResolver.class.getClassLoader().getResource(path.substring(10)).getFile();
		}
		return path;
	}

}
