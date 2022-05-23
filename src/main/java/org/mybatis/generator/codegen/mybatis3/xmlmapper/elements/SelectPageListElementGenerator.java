package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.plugins.AnywidePlugin;

//jackson.song add
public class SelectPageListElementGenerator extends AbstractXmlElementGenerator {
	public void addElements(XmlElement parentElement) {
		XmlElement answer = new XmlElement("select");
		answer.addAttribute(new Attribute("id", this.introspectedTable.getSelectPageListStatementId()));
		if (this.introspectedTable.getRules().generateResultMapWithBLOBs()) {
			answer.addAttribute(new Attribute("resultMap", this.introspectedTable.getResultMapWithBLOBsId()));
		} else {
			answer.addAttribute(new Attribute("resultMap", this.introspectedTable.getBaseResultMapId()));
		}
		this.context.getCommentGenerator().addComment(answer);
//    String productName = this.introspectedTable.getFullyQualifiedTable().getProductName();
//    if ("mysql".equalsIgnoreCase(productName)) {
		mysql(answer, parentElement);
//    } else if ("microsoft sql server".equalsIgnoreCase(productName)) {
//      microsoft_sql_server(answer, parentElement);
//    } else {
//      orcale(answer, parentElement);
//    }
	}

	private void mysql(XmlElement answer, XmlElement parentElement) {
		FullyQualifiedJavaType rowType = introspectedTable.getRules().calculateAllFieldsClass();
		String variableName = AnywidePlugin.lowerFirst(rowType.getShortName());
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		if (StringUtility.stringHasValue(this.introspectedTable.getSelectByPrimaryKeyQueryId())) {
			sb.append("'");
			sb.append(this.introspectedTable.getSelectByPrimaryKeyQueryId());
			sb.append("' as QUERYID,");
		}
		answer.addElement(new TextElement(sb.toString()));
		answer.addElement(getBaseColumnListElement());
		if (this.introspectedTable.hasBLOBColumns()) {
			answer.addElement(new TextElement(","));
			answer.addElement(getBlobColumnListElement());
		}
		sb.setLength(0);
		sb.append("from ");
		sb.append(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
		answer.addElement(new TextElement(sb.toString()));
		XmlElement whereElement = new XmlElement("where");
		for (int i = 0; i < this.introspectedTable.getAllColumns().size(); i++) {
			IntrospectedColumn column = this.introspectedTable.getAllColumns().get(i);
			XmlElement isNotNullElement = new XmlElement("if");
			sb.setLength(0);
			sb.append(variableName);
			sb.append(".");
			sb.append(column.getJavaProperty());
			sb.append(" != null");
			isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
			sb.setLength(0);
			sb.append(" and ");
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
			sb.append(" = ");
			sb.append("#{");
			sb.append(variableName);
			sb.append(".");
			sb.append(column.getJavaProperty());
			sb.append(",");
			sb.append("jdbcType=");
			sb.append(column.getJdbcTypeName());
			sb.append("}");
			isNotNullElement.addElement(new TextElement(sb.toString()));
			whereElement.addElement(isNotNullElement);
		}
		answer.addElement(whereElement);
		sb.setLength(0);
//		sb.append("order by ");
//		if (!this.introspectedTable.getPrimaryKeyColumns().isEmpty()) {
//			sb.append(MyBatis3FormattingUtilities
//					.getAliasedEscapedColumnName(this.introspectedTable.getPrimaryKeyColumns().get(0)));
//		} else {
//			sb.append(MyBatis3FormattingUtilities
//					.getAliasedEscapedColumnName(this.introspectedTable.getBaseColumns().get(0)));
//		}
//		sb.append("\n    ");
		sb.append(" limit #{page.start},#{page.row}");
		answer.addElement(new TextElement(sb.toString()));
		if (this.context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(answer, this.introspectedTable))
			parentElement.addElement(answer);
	}

	private void oracle(XmlElement answer, XmlElement parentElement) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM(SELECT A.*,ROWNUM  num FROM");
		sb.append(" (");
		sb.append("select ");
		if (StringUtility.stringHasValue(this.introspectedTable.getSelectByPrimaryKeyQueryId())) {
			sb.append('\'');
			sb.append(this.introspectedTable.getSelectByPrimaryKeyQueryId());
			sb.append("' as QUERYID,");
		}
		answer.addElement(new TextElement(sb.toString()));
		answer.addElement(getBaseColumnListElement());
		if (this.introspectedTable.hasBLOBColumns()) {
			answer.addElement(new TextElement(","));
			answer.addElement(getBlobColumnListElement());
		}
		sb.setLength(0);
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
			sb.append("#{ ");
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
			sb.append(",");
			sb.append("jdbcType=");
			sb.append(column.getJdbcTypeName());
			sb.append("}");
			isNotNullElement.addElement(new TextElement(sb.toString()));
			whereElement.addElement(isNotNullElement);
		}
		answer.addElement(whereElement);
		sb.setLength(0);
		sb.append("order by ");
		if (!this.introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			sb.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(this.introspectedTable.getPrimaryKeyColumns().get(0)));
		} else {
			sb.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(this.introspectedTable.getBaseColumns().get(0)));
		}
		sb.append("\n    ");
		sb.append(" )A");
		sb.append(" WHERE <![CDATA[ ROWNUM<=${page.end} ]]> ) WHERE num>=${page.start}");
		answer.addElement(new TextElement(sb.toString()));
		if (this.context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(answer, this.introspectedTable))
			parentElement.addElement(answer);
	}

	private void microsoft_sql_server(XmlElement answer, XmlElement parentElement) {
		StringBuilder sb = new StringBuilder();
		sb.append("select Top ${page.row}");
		if (StringUtility.stringHasValue(this.introspectedTable.getSelectByPrimaryKeyQueryId())) {
			sb.append('\'');
			sb.append(this.introspectedTable.getSelectByPrimaryKeyQueryId());
			sb.append("' as QUERYID,");
		}
		answer.addElement(new TextElement(sb.toString()));
		answer.addElement(getBaseColumnListElement());
		if (this.introspectedTable.hasBLOBColumns()) {
			answer.addElement(new TextElement(","));
			answer.addElement(getBlobColumnListElement());
		}
		sb.setLength(0);
		sb.append("from ");
		sb.append(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
		answer.addElement(new TextElement(sb.toString()));
		XmlElement whereElement = new XmlElement("where");
		for (int i = 0; i < this.introspectedTable.getAllColumns().size(); i++) {
			IntrospectedColumn column = this.introspectedTable.getAllColumns().get(i);
			XmlElement isNotNullElement = new XmlElement("if");
			sb.setLength(0);
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
			sb.append(" != null");
			isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
			sb.setLength(0);
			sb.append(" and ");
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
			if (i == 0) {
				sb.append(" like '%'");
				sb.append("#{ ");
				sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
				sb.append(",");
				sb.append("jdbcType=");
				sb.append(column.getJdbcTypeName());
				sb.append("}");
				sb.append("'%'");
			} else {
				sb.append(" = ");
				sb.append("#{ ");
				sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
				sb.append(",");
				sb.append("jdbcType=");
				sb.append(column.getJdbcTypeName());
				sb.append("}");
			}
			isNotNullElement.addElement(new TextElement(sb.toString()));
			whereElement.addElement(isNotNullElement);
		}
		sb.setLength(0);
		IntrospectedColumn introspectedColumn = this.introspectedTable.getBaseColumns().get(0);
		IntrospectedColumn columns = null;
		if (!this.introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			columns = this.introspectedTable.getPrimaryKeyColumns().get(0);
		} else {
			columns = introspectedColumn;
		}
		sb.append(" and ");
		sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(columns));
		sb.append(" not in (");
		sb.append("select Top ${page.start} ");
		sb.append(columns.getActualColumnName());
		sb.append(" from ");
		sb.append(this.introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
		whereElement.addElement(new TextElement(sb.toString()));
		sb.setLength(0);
		XmlElement where = new XmlElement("where");
		for (int j = 0; j < this.introspectedTable.getAllColumns().size(); j++) {
			IntrospectedColumn column = this.introspectedTable.getAllColumns().get(j);
			XmlElement isNotNullElement = new XmlElement("if");
			sb.setLength(0);
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
			sb.append(" != null");
			isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
			sb.setLength(0);
			sb.append(" and ");
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
			if (j == 0) {
				sb.append(" like '%'");
				sb.append("#{ ");
				sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
				sb.append(",");
				sb.append("jdbcType=");
				sb.append(column.getJdbcTypeName());
				sb.append("}");
				sb.append("'%'");
			} else {
				sb.append(" = ");
				sb.append("#{ ");
				sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column));
				sb.append(",");
				sb.append("jdbcType=");
				sb.append(column.getJdbcTypeName());
				sb.append("}");
			}
			isNotNullElement.addElement(new TextElement(sb.toString()));
			where.addElement(isNotNullElement);
		}
		whereElement.addElement(where);
		sb.setLength(0);
		sb.append("order by ");
		if (!this.introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			sb.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(this.introspectedTable.getPrimaryKeyColumns().get(0)));
		} else {
			sb.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(this.introspectedTable.getBaseColumns().get(0)));
		}
		sb.append(" )");
		whereElement.addElement(new TextElement(sb.toString()));
		sb.setLength(0);
		sb.append("order by ");
		if (!this.introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			sb.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(this.introspectedTable.getPrimaryKeyColumns().get(0)));
		} else {
			sb.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(this.introspectedTable.getBaseColumns().get(0)));
		}
		answer.addElement(whereElement);
		answer.addElement(new TextElement(sb.toString()));
		if (this.context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(answer, this.introspectedTable))
			parentElement.addElement(answer);
	}
}
