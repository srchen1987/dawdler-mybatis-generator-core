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
package org.mybatis.generator.codegen.mybatis3.javamapper.elements.annotated;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.InsertSelectiveMethodGenerator;

public class AnnotatedInsertSelectiveMethodGenerator extends InsertSelectiveMethodGenerator {

	public AnnotatedInsertSelectiveMethodGenerator() {
		super();
	}

	@Override
	public void addMapperAnnotations(Method method) {
		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getMyBatis3SqlProviderType());

		String s = "@InsertProvider(type=" //$NON-NLS-1$
				+ fqjt.getShortName() + ".class, method=\"" //$NON-NLS-1$
				+ introspectedTable.getInsertSelectiveStatementId() + "\")"; //$NON-NLS-1$
		method.addAnnotation(s);

		buildGeneratedKeyAnnotation().ifPresent(method::addAnnotation);
	}

	@Override
	public void addExtraImports(Interface interfaze) {
		interfaze.addImportedTypes(buildGeneratedKeyImportsIfRequired());
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider")); //$NON-NLS-1$
	}
}
