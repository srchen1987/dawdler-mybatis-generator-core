/*
 *    Copyright 2006-2021 the original author or authors.
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
package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

public class UpdateByExampleWithoutBLOBsMethodGenerator extends AbstractJavaMapperMethodGenerator {

	public UpdateByExampleWithoutBLOBsMethodGenerator() {
		super();
	}

	@Override
	public void addInterfaceElements(Interface interfaze) {
		String statementId = introspectedTable.getUpdateByExampleStatementId();
		FullyQualifiedJavaType parameterType;
		if (introspectedTable.getRules().generateBaseRecordClass()) {
			parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		} else {
			parameterType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
		}
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

		Method method = buildBasicUpdateByExampleMethod(statementId, parameterType, importedTypes);

		addMapperAnnotations(method);

		if (context.getPlugins().clientUpdateByExampleWithoutBLOBsMethodGenerated(method, interfaze,
				introspectedTable)) {
			addExtraImports(interfaze);
			interfaze.addImportedTypes(importedTypes);
			interfaze.addMethod(method);
		}
	}

	public void addMapperAnnotations(Method method) {
		// extension point for subclasses
	}

	public void addExtraImports(Interface interfaze) {
		// extension point for subclasses
	}
}
