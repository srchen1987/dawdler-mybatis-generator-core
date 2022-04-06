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
package org.mybatis.generator.codegen.mybatis3.javamapper.elements.sqlprovider;

import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getEscapedColumnName;
import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getParameterClause;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getGetterMethodName;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

import java.util.Set;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;

public class ProviderInsertSelectiveMethodGenerator extends AbstractJavaProviderMethodGenerator {

	@Override
	public void addClassElements(TopLevelClass topLevelClass) {
		FullyQualifiedJavaType fqjt = introspectedTable.getRules().calculateAllFieldsClass();
		Set<FullyQualifiedJavaType> importedTypes = initializeImportedTypes(fqjt);

		Method method = new Method(introspectedTable.getInsertSelectiveStatementId());
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getStringInstance());
		method.addParameter(new Parameter(fqjt, "row")); //$NON-NLS-1$

		context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

		method.addBodyLine("SQL sql = new SQL();"); //$NON-NLS-1$

		method.addBodyLine(String.format("sql.INSERT_INTO(\"%s\");", //$NON-NLS-1$
				escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime())));

		for (IntrospectedColumn introspectedColumn : ListUtilities
				.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns())) {

			method.addBodyLine(""); //$NON-NLS-1$
			if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()
					&& !introspectedColumn.isSequenceColumn()) {
				method.addBodyLine(String.format("if (row.%s() != null) {", //$NON-NLS-1$
						getGetterMethodName(introspectedColumn.getJavaProperty(),
								introspectedColumn.getFullyQualifiedJavaType())));
			}
			method.addBodyLine(String.format("sql.VALUES(\"%s\", \"%s\");", //$NON-NLS-1$
					escapeStringForJava(getEscapedColumnName(introspectedColumn)),
					getParameterClause(introspectedColumn)));

			if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()
					&& !introspectedColumn.isSequenceColumn()) {
				method.addBodyLine("}"); //$NON-NLS-1$
			}
		}

		method.addBodyLine(""); //$NON-NLS-1$
		method.addBodyLine("return sql.toString();"); //$NON-NLS-1$

		if (context.getPlugins().providerInsertSelectiveMethodGenerated(method, topLevelClass, introspectedTable)) {
			topLevelClass.addImportedTypes(importedTypes);
			topLevelClass.addMethod(method);
		}
	}
}
