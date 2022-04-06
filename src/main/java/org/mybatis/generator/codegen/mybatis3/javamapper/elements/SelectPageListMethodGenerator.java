package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.plugins.AnywidePlugin;

public class SelectPageListMethodGenerator extends AbstractJavaMapperMethodGenerator {
	private boolean isSimple;
	private FullyQualifiedJavaType paramAnnotationType;
	FullyQualifiedJavaType pageType;

	public SelectPageListMethodGenerator(boolean isSimple) {
		this.isSimple = isSimple;
		this.paramAnnotationType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
		this.pageType = new FullyQualifiedJavaType("com.anywide.dawdler.serverplug.load.bean.Page");
	}

	public void addInterfaceElements(Interface interfaze) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
		Method method = new Method(this.introspectedTable.getSelectPageListStatementId());
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setAbstract(true);
		FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("List");
		returnType.addTypeArgument(this.introspectedTable.getRules().calculateAllFieldsClass());
		method.setReturnType(returnType);
		importedTypes.add(returnType);
		importedTypes.add(paramAnnotationType);
		if (this.isSimple && this.introspectedTable.getRules().generateSelectPageList()) {
			FullyQualifiedJavaType rowType = introspectedTable.getRules().calculateAllFieldsClass();
			String variableName = AnywidePlugin.lowerFirst(rowType.getShortName());
			Parameter row = new Parameter(rowType, variableName, "@Param(\"" + variableName + "\")");
			Parameter page = new Parameter(pageType, "page", "@Param(\"page\")");
			method.addParameter(0, row);
			method.addParameter(1, page);
			importedTypes.add(rowType);
			importedTypes.add(pageType);
		}
		this.context.getCommentGenerator().addGeneralMethodComment(method, this.introspectedTable);
		if (this.context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(method, interfaze,
				this.introspectedTable)) {
			interfaze.addImportedTypes(importedTypes);
			interfaze.addMethod(method);
		}
	}

}
