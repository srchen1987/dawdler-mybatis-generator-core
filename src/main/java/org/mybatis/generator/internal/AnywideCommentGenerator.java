/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mybatis.generator.internal;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

//jackson.song add
public class AnywideCommentGenerator implements CommentGenerator {

	private Properties properties;
	private boolean suppressDate;
	private boolean suppressAllComments;
	private String company = null;
	/**
	 * The addition of table remark's comments. If suppressAllComments is true, this
	 * option is ignored
	 */
	private boolean addRemarkComments;

	public AnywideCommentGenerator() {
		super();
		properties = new Properties();
		suppressDate = false;
		suppressAllComments = false;
		addRemarkComments = false;
	}

	public void addJavaFileComment(CompilationUnit compilationUnit) {
		// add no file level comments by default
		return;
	}

	/**
	 * Adds a suitable comment to warn users that the element was generated, and
	 * when it was generated.
	 */
	public void addComment(XmlElement xmlElement) {
		if (suppressAllComments) {
			return;
		}
	}

	public void addRootComment(XmlElement rootElement) {
		// add no document level comments by default
		return;
	}

	public void addConfigurationProperties(Properties properties) {
		this.properties.putAll(properties);

		suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

		suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

		addRemarkComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

		company = properties.getProperty("company");

	}

	/**
	 * This method adds the custom javadoc tag for. You may do nothing if you do not
	 * wish to include the Javadoc tag - however, if you do not include the Javadoc
	 * tag then the Java merge capability of the eclipse plugin will break.
	 *
	 * @param javaElement the java element
	 */
	protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
		javaElement.addJavaDocLine(" *"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		sb.append(" * "); //$NON-NLS-1$
		sb.append(MergeConstants.NEW_ELEMENT_TAG);
		if (markAsDoNotDelete) {
			sb.append(" do_not_delete_during_merge"); //$NON-NLS-1$
		}
		String s = getDateString();
		if (s != null) {
			sb.append(' ');
			sb.append(s);
		}
		javaElement.addJavaDocLine(sb.toString());
	}

	/**
	 * This method returns a formated date string to include in the Javadoc tag and
	 * XML comments. You may return null if you do not want the date in these
	 * documentation elements.
	 *
	 * @return a string representing the current timestamp, or null
	 */
	protected String getDateString() {
		if (suppressDate) {
			return null;
		} else {
			return new Date().toString();
		}
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("/** "); //$NON-NLS-1$
		sb.append(strFilter(introspectedTable.getRemarks()));
		sb.append(" */");
		innerClass.addJavaDocLine(sb.toString());
	}

	public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("/** "); //$NON-NLS-1$
		sb.append(strFilter(introspectedTable.getRemarks()));
		sb.append(" */");
		innerEnum.addJavaDocLine(sb.toString());
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("/** "); //$NON-NLS-1$
		sb.append(strFilter(introspectedColumn.getRemarks()));
		sb.append(" */");
		field.addJavaDocLine(sb.toString());
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("/** "); //$NON-NLS-1$
		sb.append(strFilter(introspectedTable.getRemarks()));
		sb.append(" */");
		field.addJavaDocLine(sb.toString());
	}

	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
		Map<String, String> remarks = allColumns.stream()
				.collect(Collectors.toMap(IntrospectedColumn::getJavaProperty, IntrospectedColumn::getRemarks));

		String name = method.getName().toLowerCase();
		String prefix = null;
		String suffix = null;
		if (name.startsWith("select")) {
			prefix = "查询";
			if (name.endsWith("list"))
				suffix = "列表";
		} else if (name.startsWith("insert")) {
			prefix = "插入";
		} else if (name.startsWith("update")) {
			prefix = "更新";
		} else if (name.startsWith("delete")) {
			prefix = "删除";
		} else if (name.startsWith("list")) {
			prefix = "查询";
			suffix = "列表";
		} else if (name.startsWith("info")) {
			prefix = "查询";
		}

		Calendar calendar = Calendar.getInstance();
		String date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DATE);
		StringBuilder sb = new StringBuilder();
		method.addJavaDocLine("/** "); //$NON-NLS-1$
		method.addJavaDocLine(" * ");
		sb.append(" * @Title ");
		sb.append(method.getName());
		method.addJavaDocLine(sb.toString());
		sb.setLength(0);
		sb.append(" * @Description ");
		if (prefix != null)
			sb.append(prefix);
		if (introspectedTable.getRemarks() != null && !"".equals(introspectedTable.getRemarks().trim())) {
			sb.append("[");
			sb.append(introspectedTable.getRemarks());
			sb.append("]");
		}
		if (suffix != null)
			sb.append(suffix);
		method.addJavaDocLine(sb.toString());

		sb.setLength(0);
		if (company != null) {
			sb.append(" * @Copyright	");
			sb.append(company);
			sb.append("	");
			sb.append(date);
			method.addJavaDocLine(sb.toString());
			method.addJavaDocLine(" * <p> Company: " + company + " </p>");
		}
		method.addJavaDocLine(" * @author " + System.getProperty("user.name"));
		method.addJavaDocLine(" * @date "
				+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new java.util.Date()));
		method.addJavaDocLine(" * @version 1.0");
		for (Parameter param : method.getParameters()) {
			String remark = remarks.get(param.getName());
			if (remark != null && !remark.trim().equals("")) {
				method.addJavaDocLine(" * @param " + param.getName() + "	" + strFilter(remark));
			} else {
				method.addJavaDocLine(" * @param " + param.getName());
			}
		}
		method.addJavaDocLine(" */");
	}

	public void addGetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		method.addJavaDocLine("/**"); //$NON-NLS-1$
		sb.append(" * ");
		sb.append("获取");
		sb.append("[");
		sb.append(strFilter(introspectedColumn.getRemarks()));
		sb.append("]");
		method.addJavaDocLine(sb.toString());

		sb.setLength(0);
		sb.append(" * @return "); //$NON-NLS-1$
		sb.append(introspectedColumn.getActualColumnName());
		method.addJavaDocLine(sb.toString());
		method.addJavaDocLine(" */"); //$NON-NLS-1$
	}

	public void addSetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		method.addJavaDocLine("/**"); //$NON-NLS-1$
		sb.append(" * ");
		sb.append("设置");
		sb.append("[");
		sb.append(strFilter(introspectedColumn.getRemarks()));
		sb.append("]");
		method.addJavaDocLine(sb.toString());

		Parameter parm = method.getParameters().get(0);
		sb.setLength(0);
		sb.append(" * @param "); //$NON-NLS-1$
		sb.append(parm.getName());
		method.addJavaDocLine(sb.toString());
		method.addJavaDocLine(" */"); //$NON-NLS-1$
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
		if (suppressAllComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("/** "); //$NON-NLS-1$
		sb.append(strFilter(introspectedTable.getRemarks()));
		sb.append(" */");
		innerClass.addJavaDocLine(sb.toString());

		addJavadocTag(innerClass, markAsDoNotDelete);

		innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
	}

	public static String strFilter(String str) {
		if (str == null)
			return "";
		int index = str.lastIndexOf("{");
		if (index != -1) {
			str = str.substring(0, str.lastIndexOf("{"));
		}
		return str;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mybatis.generator.api.CommentGenerator#addTopLevelClassComment(org.
	 * mybatis.generator.api.dom.java.TopLevelClass,
	 * org.mybatis.generator.api.IntrospectedTable)
	 */
	@Override
	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		if (suppressAllComments || !addRemarkComments) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		topLevelClass.addJavaDocLine("/**"); //$NON-NLS-1$

		String remarks = introspectedTable.getRemarks();
		if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
			topLevelClass.addJavaDocLine(" * Database Table Remarks:");
			String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
			for (String remarkLine : remarkLines) {
				topLevelClass.addJavaDocLine(" *   " + remarkLine); //$NON-NLS-1$
			}
		}
		topLevelClass.addJavaDocLine(" *"); //$NON-NLS-1$

		topLevelClass.addJavaDocLine(" * This class was generated by MyBatis Generator."); //$NON-NLS-1$

		sb.append(" * This class corresponds to the database table "); //$NON-NLS-1$
		sb.append(introspectedTable.getFullyQualifiedTable());
		topLevelClass.addJavaDocLine(sb.toString());

		addJavadocTag(topLevelClass, true);

		topLevelClass.addJavaDocLine(" */"); //$NON-NLS-1$
	}

	@Override
	public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
			Set<FullyQualifiedJavaType> imports) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
			Set<FullyQualifiedJavaType> imports) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable,
			Set<FullyQualifiedJavaType> imports) {
		// TODO Auto-generated method stub

	}

}
