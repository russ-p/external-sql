package com.github.russ_p.externalsql;

import java.io.IOException;
import java.util.Objects;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ImplementationDefinition.Optional;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class ExternalSQLFactory {

	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> clazz) throws IOException, InstantiationException, IllegalAccessException {
		ExternalSQL anno = clazz.getAnnotation(ExternalSQL.class);
		Objects.requireNonNull(anno, "Class must be annotated with @ExternalSQL");

		String srcFile = anno.value();
		SqlResolver resolver = new SqlResolver(srcFile);

		Optional<Object> implement = new ByteBuddy()
				.subclass(Object.class)
				.implement(clazz);

		ReceiverTypeDefinition<Object> intercept = implement
				.method(ElementMatchers.named("toString"))
				.intercept(FixedValue.value("SQL queries from " + srcFile));

		for (String name : resolver.getQueryNames()) {
			intercept = intercept
					.method(ElementMatchers.named(name))
					.intercept(FixedValue.value(resolver.getQuery(name)));
		}

		Class<?> dynamicType = intercept
				.make()
				.load(getClass().getClassLoader())
				.getLoaded();

		return (T) dynamicType.newInstance();
	}

}
