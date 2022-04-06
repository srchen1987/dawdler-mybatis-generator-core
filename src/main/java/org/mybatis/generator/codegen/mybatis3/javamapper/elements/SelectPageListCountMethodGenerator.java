package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.plugins.AnywidePlugin;

public class SelectPageListCountMethodGenerator extends AbstractJavaMapperMethodGenerator {
	private boolean isSimple;

	public SelectPageListCountMethodGenerator(boolean isSimple) {
		this.isSimple = isSimple;
	}

	public void addInterfaceElements(Interface interfaze) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
		Method method = new Method(this.introspectedTable.getSelectPageListCountStatementId());
		method.setVisibility(JavaVisibility.PUBLIC);
		FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getIntInstance();
		method.setReturnType(returnType);
		method.setAbstract(true);
		FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
		if (this.isSimple && this.introspectedTable.getRules().generateSelectPageListCount()) {
			String variableName = AnywidePlugin.lowerFirst(parameterType.getShortName());
			method.addParameter(new Parameter(parameterType, variableName));
			importedTypes.add(parameterType);
		}
		this.context.getCommentGenerator().addGeneralMethodComment(method, this.introspectedTable);
		if (this.context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(method, interfaze,
				this.introspectedTable)) {
			interfaze.addImportedTypes(importedTypes);
			interfaze.addMethod(method);
		}
	}
}
