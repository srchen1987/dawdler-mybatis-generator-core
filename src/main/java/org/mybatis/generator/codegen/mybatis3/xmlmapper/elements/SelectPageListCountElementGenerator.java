package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

//jackson.song add
public class SelectPageListCountElementGenerator extends AbstractXmlElementGenerator {
	public void addElements(XmlElement parentElement) {
		XmlElement answer = new XmlElement("select");
		answer.addAttribute(new Attribute("id", this.introspectedTable.getSelectPageListCountStatementId()));
		String parameterType = introspectedTable.getBaseRecordType();
		answer.addAttribute(new Attribute("parameterType", parameterType));
		answer.addAttribute(new Attribute("resultType", FullyQualifiedJavaType.getIntInstance().toString()));
		this.context.getCommentGenerator().addComment(answer);
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) ");
		sb.append("from ");
		sb.append(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
		answer.addElement(new TextElement(sb.toString()));
		XmlElement whereElement = new XmlElement("where");
		for (int i = 0; i < this.introspectedTable.getAllColumns().size(); i++) {
			IntrospectedColumn column = this.introspectedTable.getAllColumns().get(i);
			XmlElement isNotNullElement = new XmlElement("if");
			sb.setLength(0);
			sb.append(column.getJavaProperty());
			sb.append(" != null");
			isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
			sb.setLength(0);
			sb.append(" and ");
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
			sb.append(" = ");
			sb.append("#{");
			sb.append(column.getJavaProperty());
			sb.append(",");
			sb.append("jdbcType=");
			sb.append(column.getJdbcTypeName());
			sb.append("}");
			isNotNullElement.addElement(new TextElement(sb.toString()));
			whereElement.addElement(isNotNullElement);
		}
		answer.addElement(whereElement);
		if (this.context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(answer, this.introspectedTable))
			parentElement.addElement(answer);
	}
}
