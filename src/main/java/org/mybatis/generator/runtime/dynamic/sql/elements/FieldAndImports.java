/*
 *    Copyright 2006-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.runtime.dynamic.sql.elements;

import java.util.HashSet;
import java.util.Set;

import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

public class FieldAndImports {

	private final Field field;
	private final Set<FullyQualifiedJavaType> imports;

	private FieldAndImports(Builder builder) {
		field = builder.field;
		imports = builder.imports;
	}

	public Field getField() {
		return field;
	}

	public Set<FullyQualifiedJavaType> getImports() {
		return imports;
	}

	public static Builder withField(Field field) {
		return new Builder().withField(field);
	}

	public static class Builder {
		private Field field;
		private final Set<FullyQualifiedJavaType> imports = new HashSet<>();

		public Builder withField(Field field) {
			this.field = field;
			return this;
		}

		public Builder withImports(Set<FullyQualifiedJavaType> imports) {
			this.imports.addAll(imports);
			return this;
		}

		public FieldAndImports build() {
			return new FieldAndImports(this);
		}
	}
}
