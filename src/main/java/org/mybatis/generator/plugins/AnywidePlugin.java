package org.mybatis.generator.plugins;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.internal.util.StringUtility;

//jackson.song add
public class AnywidePlugin extends PluginAdapter {

	private FullyQualifiedJavaType controllerType;
	private FullyQualifiedJavaType serviceType;
	private FullyQualifiedJavaType serviceImplType;
	private FullyQualifiedJavaType daoType;
	private FullyQualifiedJavaType pojoType;
	private FullyQualifiedJavaType pageType;
	private FullyQualifiedJavaType listType;
	private FullyQualifiedJavaType pojoCriteriaType;
	private FullyQualifiedJavaType localOrRemoteServiceAnnotationType;
	private FullyQualifiedJavaType repositoryAnnotationType;
	private FullyQualifiedJavaType dBTransactionAnnotationType;
	private FullyQualifiedJavaType dBTransactionMODEType;

	private FullyQualifiedJavaType controllerAnnotationType;
	private FullyQualifiedJavaType requestMappingAnnotationType;
	private FullyQualifiedJavaType responseBodyAnnotationType;
	private FullyQualifiedJavaType requestMethodType;
	private FullyQualifiedJavaType pageResultType;
	private FullyQualifiedJavaType baseResultType;
	private FullyQualifiedJavaType baseVoidResultType;
	private FullyQualifiedJavaType serviceAnnotationType;

	private String controllerPack;
	private String servicePack;
	private String serviceImplPack;
	private String apiProject;
	private String serviceProject;
	private String webProject;

	private String pojoUrl;
	private String prefix;
	private String schemaLocation;

	/**
	 * 是否添加注解
	 */
	private boolean enableInfo = true;
	private boolean enableInsert = true;
	private boolean enableUpdate = true;
	private boolean enableDelete = true;
	private boolean enableList = true;
	private boolean enableValidator = true;
	private String chanelGroupId = null;

	public AnywidePlugin() {
		super();
		this.prefix = "/";
	}

	@Override
	public boolean validate(List<String> warnings) {
		String enableInfo = properties.getProperty("enableInfo");
		String enableInsert = properties.getProperty("enableInsert");
		String enableUpdate = properties.getProperty("enableUpdate");
		String enableDelete = properties.getProperty("enableDelete");
		String enableList = properties.getProperty("enableList");
		String enableValidator = properties.getProperty("enableValidator");
		chanelGroupId = properties.getProperty("chanelGroupId");
		String serviceType = (String) context.getProperties().get("serviceType");
		boolean localService = true;
		if (serviceType != null && serviceType.equals("remote")) {
			localService = false;
		}
		if (StringUtility.stringHasValue(enableInfo))
			this.enableInfo = StringUtility.isTrue(enableInfo);
		if (StringUtility.stringHasValue(enableInsert))
			this.enableInsert = StringUtility.isTrue(enableInsert);
		if (StringUtility.stringHasValue(enableUpdate))
			this.enableUpdate = StringUtility.isTrue(enableUpdate);
		if (StringUtility.stringHasValue(enableDelete))
			this.enableDelete = StringUtility.isTrue(enableDelete);
		if (StringUtility.stringHasValue(enableList))
			this.enableList = StringUtility.isTrue(enableList);
		if (StringUtility.stringHasValue(enableValidator))
			this.enableValidator = StringUtility.isTrue(enableValidator);

		// 生成文件路径
		controllerPack = isNull(properties.getProperty("targetPackageController"));
		if ("".equals(controllerPack))
			this.enableValidator = false;
		servicePack = isNull(properties.getProperty("targetPackageService"));
		serviceImplPack = properties.getProperty("targetPackageServiceImpl");
		apiProject = (String) context.getProperties().get("apiProject");
		serviceProject = (String) context.getProperties().get("serviceProject");
		webProject = (String) context.getProperties().get("webProject");
		schemaLocation = properties.getProperty("schemaLocation");
		pojoUrl = context.getJavaModelGeneratorConfiguration().getTargetPackage();
		if (localService) {
			localOrRemoteServiceAnnotationType = new FullyQualifiedJavaType(
					"club.dawdler.local.service.annotation.LocalService");
		} else {
			localOrRemoteServiceAnnotationType = new FullyQualifiedJavaType(
					"club.dawdler.remote.service.annotation.RemoteService");
		}

		controllerAnnotationType = new FullyQualifiedJavaType(
				"club.dawdler.clientplug.web.annotation.Controller");
		responseBodyAnnotationType = new FullyQualifiedJavaType(
				"club.dawdler.clientplug.web.annotation.ResponseBody");
		requestMappingAnnotationType = new FullyQualifiedJavaType(
				"club.dawdler.clientplug.web.annotation.RequestMapping");
		serviceAnnotationType = new FullyQualifiedJavaType("club.dawdler.core.service.annotation.Service");
		repositoryAnnotationType = new FullyQualifiedJavaType(
				"club.dawdler.core.db.annotation.Repository");
		dBTransactionAnnotationType = new FullyQualifiedJavaType(
				"club.dawdler.core.db.annotation.DBTransaction");
		dBTransactionMODEType = new FullyQualifiedJavaType(
				"club.dawdler.core.db.annotation.DBTransaction.MODE");
		return true;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
		String table = introspectedTable.getBaseRecordType();
		String tableName = table.replaceAll(this.pojoUrl + ".", "");
		if (serviceImplPack == null)
			serviceImplPack = servicePack + ".impl";
		// controller
		controllerType = new FullyQualifiedJavaType(controllerPack + "." + tableName + "Controller");
		// service
		serviceType = new FullyQualifiedJavaType(servicePack + "." + tableName + "Service");
		// serviceImpl
		serviceImplType = new FullyQualifiedJavaType(serviceImplPack + "." + tableName + "ServiceImpl");
		// dao
		daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
		if (introspectedTable.hasBLOBColumns()) {
			pojoType = new FullyQualifiedJavaType(
					pojoUrl + "." + tableName + "WithBLOBs");
		} else {
			pojoType = new FullyQualifiedJavaType(
					pojoUrl + "." + tableName);
		}
		// 分页
		pageType = new FullyQualifiedJavaType("club.dawdler.serverplug.load.bean.Page");
		// list
		listType = FullyQualifiedJavaType.getNewListInstance();

		requestMethodType = new FullyQualifiedJavaType(
				"club.dawdler.clientplug.web.annotation.RequestMapping.RequestMethod");

		pageResultType = new FullyQualifiedJavaType("club.dawdler.core.result.PageResult");
		FullyQualifiedJavaType pojoListType = new FullyQualifiedJavaType("List");
		pojoListType.addTypeArgument(pojoType);
		pageResultType.addTypeArgument(pojoListType);
		baseResultType = new FullyQualifiedJavaType("club.dawdler.core.result.BaseResult");
		baseResultType.addTypeArgument(pojoType);
		baseVoidResultType = new FullyQualifiedJavaType("club.dawdler.core.result.BaseResult");
		baseVoidResultType.addTypeArgument(new FullyQualifiedJavaType("java.lang.Void"));
		if (controllerPack != null && !"".equals(controllerPack)) {
			TopLevelClass topLevelClass = new TopLevelClass(controllerType);
			// 引入包
			addControllerImport(topLevelClass);
			// 生成Controller
			addController(topLevelClass, introspectedTable, tableName, files);
		}
		if (servicePack != null && !"".equals(servicePack)) {
			// 生成Service
			Interface _interface = new Interface(serviceType);
			TopLevelClass topLevelClass = new TopLevelClass(serviceImplType);
			// 引入包
			addServiceImport(_interface, topLevelClass, introspectedTable);
			addService(_interface, introspectedTable, tableName, files);
			addServiceImpl(topLevelClass, introspectedTable, tableName, files);
		}

		return files;
	}

	/**
	 * 添加实现类
	 *
	 * @param introspectedTable
	 * @param tableName
	 * @param files
	 */
	private void addController(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName,
			List<GeneratedJavaFile> files) {

		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		topLevelClass.addAnnotation("@Controller");
		topLevelClass.addAnnotation("@RequestMapping(\"" + prefix + toLowerCase(tableName) + "\")");
		topLevelClass.addImportedType(controllerAnnotationType);
		// 添加引用service
		addField(topLevelClass, serviceType, "@" + localOrRemoteServiceAnnotationType.getShortName());
		// 添加方法
		Method method = null;

		boolean isPrimaryKey = !introspectedTable.getPrimaryKeyColumns().isEmpty();

		if (enableList) {
			method = selectList(introspectedTable, tableName);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			topLevelClass.addMethod(method);
		}
		if (enableInfo && isPrimaryKey) {
			method = selectInfo(introspectedTable, tableName);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			topLevelClass.addMethod(method);
		}
		if (enableUpdate && isPrimaryKey) {
			method = update(introspectedTable, tableName);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			topLevelClass.addMethod(method);
		}
		if (enableInsert) {
			method = insert(introspectedTable, tableName);

			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			topLevelClass.addMethod(method);

		}
		if (enableDelete && isPrimaryKey) {
			method = delete(introspectedTable, tableName);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			topLevelClass.addMethod(method);
		}

		// 生成文件
		GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, webProject, context.getJavaFormatter());
		files.add(file);
	}

	private void addField(TopLevelClass topLevelClass, FullyQualifiedJavaType fullyQualifiedJavaType,
			String annotation) {
		Field field = new Field(toLowerCase(fullyQualifiedJavaType.getShortName()), fullyQualifiedJavaType);
		topLevelClass.addImportedType(fullyQualifiedJavaType);
		field.setVisibility(JavaVisibility.PRIVATE);
		field.addAnnotation(annotation);
		topLevelClass.addField(field);
	}

	private Method selectList(IntrospectedTable introspectedTable, String tableName) {
		String lowerFirstName = lowerFirst(tableName);
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		String viewListName = "list";
		sb.append(viewListName);
		Method method = new Method("list");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addAnnotation("@ResponseBody");
		method.addAnnotation("@RequestMapping(value=\"" + sb + "\", method = RequestMethod.GET)");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getIntegerInstance(), "pageOn"));
		method.addParameter(new Parameter(pojoType, lowerFirstName));
		method.setReturnType(pageResultType);
		sb = new StringBuilder();
		sb.append("int row = 10;");
		sb.append("\n        ");
		sb.append("if (pageOn == null) {");
		sb.append("\n        ");
		sb.append("    pageOn = 1;");
		sb.append("\n        ");
		sb.append("}");
		sb.append("\n        ");
		sb.append("return ");
		sb.append(getServiceShort());
		sb.append("selectPageList");
		sb.append("(");
		sb.append(lowerFirstName);
		sb.append(", ");
		sb.append("pageOn");
		sb.append(", ");
		sb.append("row");
		sb.append(");");
		method.addBodyLine(sb.toString());
		return method;
	}

	public static String lowerFirst(String oldStr) {
		char[] chars = oldStr.toCharArray();
		chars[0] += 32;
		return String.valueOf(chars);
	}

	private Method selectInfo(IntrospectedTable introspectedTable, String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		String viewDetailName = "info";
		sb.append(viewDetailName);
		Method method = new Method("info");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addAnnotation("@ResponseBody");
		method.addAnnotation("@RequestMapping(value=\"" + sb + "\", method = RequestMethod.GET)");
		method.setReturnType(baseResultType);
		sb.setLength(0);
		if (!introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				String variableType = introspectedColumn.getFullyQualifiedJavaType().getShortName();
				method.addParameter(
						new Parameter(new FullyQualifiedJavaType(variableType), introspectedColumn.getJavaProperty()));
			}
		} else {
			IntrospectedColumn column = introspectedTable.getAllColumns().get(0);
			String variableType = column.getFullyQualifiedJavaType().getShortName();
			method.addParameter(new Parameter(new FullyQualifiedJavaType(variableType), column.getJavaProperty()));
		}
		sb.append("return ");
		sb.append(getServiceShort());
		sb.append("selectByPrimaryKey");
		sb.append("(");
		if (!introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				sb.append(introspectedColumn.getJavaProperty());
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
		} else {
			sb.append(introspectedTable.getAllColumns().get(0).getJavaProperty());
		}
		sb.append(");");
		method.addBodyLine(sb.toString());
		return method;
	}

	private Method update(IntrospectedTable introspectedTable, String tableName) {
		String variableName = toLowerCase(tableName);
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append("update");
		Method method = new Method("update");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addAnnotation("@ResponseBody");
		method.addAnnotation("@RequestMapping(value=\"" + sb + "\", method = RequestMethod.PUT)");
		method.setReturnType(baseVoidResultType);
		method.addParameter(new Parameter(pojoType, variableName));
		sb.setLength(0);
		sb.append("int");
		sb.append(" ");
		sb.append("count");
		sb.append(" = ");
		sb.append(getServiceShort());
		sb.append("updateByPrimaryKeySelective");
		sb.append("(");
		sb.append(variableName);
		sb.append(");");
		sb.append("\n        ");
		sb.append(baseVoidResultType.getShortName());
		sb.append(" baseResult;");
		sb.append("\n        ");
		sb.append("if(count > 0){");
		sb.append("\n         ");
		sb.append("baseResult = ");
		sb.append("new BaseResult<>(\"更新成功!\", true);");
		sb.append("\n        ");
		sb.append("}else{");
		sb.append("\n         ");
		sb.append("baseResult = ");
		sb.append("new BaseResult<>(\"更新失败!\", false);");
		sb.append("\n        ");
		sb.append("}");
		sb.append("\n        ");
		sb.append("return baseResult;");
		method.addBodyLine(sb.toString());
		return method;
	}

	private Method insert(IntrospectedTable introspectedTable, String tableName) {
		String variableName = toLowerCase(tableName);
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append("insert");
		Method method = new Method("insert");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addAnnotation("@ResponseBody");
		method.addAnnotation("@RequestMapping(value=\"" + sb + "\", method = RequestMethod.POST)");
		method.setReturnType(baseVoidResultType);
		method.addParameter(new Parameter(pojoType, variableName));
		sb.setLength(0);
		sb.append("int");
		sb.append(" ");
		sb.append("count");
		sb.append(" = ");
		sb.append(getServiceShort());
		sb.append("insertSelective");
		sb.append("(");
		sb.append(variableName);
		sb.append(");");
		sb.append("\n        ");
		sb.append(baseVoidResultType.getShortName());
		sb.append(" baseResult;");
		sb.append("\n        ");
		sb.append("if(count > 0){");
		sb.append("\n         ");
		sb.append("baseResult = ");
		sb.append("new BaseResult<>(\"插入成功!\", true);");
		sb.append("\n        ");
		sb.append("}else{");
		sb.append("\n         ");
		sb.append("baseResult = ");
		sb.append("new BaseResult<>(\"插入失败!\", false);");
		sb.append("\n        ");
		sb.append("}");
		sb.append("\n        ");
		sb.append("return baseResult;");
		method.addBodyLine(sb.toString());
		return method;
	}

	private Method delete(IntrospectedTable introspectedTable, String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append("delete");
		Method method = new Method("delete");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addAnnotation("@ResponseBody");
		method.addAnnotation("@RequestMapping(value=\"" + sb + "\", method = RequestMethod.DELETE)");
		method.setReturnType(baseVoidResultType);
		sb.setLength(0);
		if (!introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				String variableType = introspectedColumn.getFullyQualifiedJavaType().getShortName();
				method.addParameter(
						new Parameter(new FullyQualifiedJavaType(variableType), introspectedColumn.getJavaProperty()));
			}
		} else {
			IntrospectedColumn column = introspectedTable.getAllColumns().get(0);
			String variableType = column.getFullyQualifiedJavaType().getShortName();
			method.addParameter(new Parameter(new FullyQualifiedJavaType(variableType), column.getJavaProperty()));
		}
		sb.append("int");
		sb.append(" ");
		sb.append("count");
		sb.append(" = ");
		sb.append(getServiceShort());
		sb.append("deleteByPrimaryKey");
		sb.append("(");
		if (!introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				sb.append(introspectedColumn.getJavaProperty());
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
		} else {
			sb.append(introspectedTable.getAllColumns().get(0).getJavaProperty());
		}
		sb.append(");");
		sb.append("\n        ");
		sb.append(baseVoidResultType.getShortName());
		sb.append(" baseResult;");
		sb.append("\n        ");
		sb.append("if(count > 0){");
		sb.append("\n         ");
		sb.append("baseResult = ");
		sb.append("new BaseResult<>(\"删除成功!\", true);");
		sb.append("\n        ");
		sb.append("}else{");
		sb.append("\n         ");
		sb.append("baseResult = ");
		sb.append("new BaseResult<>(\"删除失败!\", false);");
		sb.append("\n        ");
		sb.append("}");
		sb.append("\n        ");
		sb.append("return baseResult;");
		method.addBodyLine(sb.toString());
		return method;
	}

	/**
	 * type 的意义 pojo 1 key 2 example 3 pojo+example 4
	 */
	private String addParams(IntrospectedTable introspectedTable, Method method, String tableName, int typeValue) {
		switch (typeValue) {
			case 1:
				method.addParameter(new Parameter(pojoType, toLowerCase(tableName)));
				return "record";
			case 2:
				StringBuilder sb = new StringBuilder();
				if (introspectedTable.getRules().generatePrimaryKeyClass()) {
					for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
						FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
						method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
					}

					for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
						sb.append(introspectedColumn.getJavaProperty());
						sb.append(",");
					}
					sb.setLength(sb.length() - 1);
				} else {
					FullyQualifiedJavaType type = introspectedTable.getAllColumns().get(0).getFullyQualifiedJavaType();
					method.addParameter(
							new Parameter(type, introspectedTable.getAllColumns().get(0).getJavaProperty()));
					sb.append(introspectedTable.getAllColumns().get(0).getJavaProperty());
				}
				return sb.toString();
			case 3:
				method.addParameter(new Parameter(pojoCriteriaType, "example"));
				return "example";
			case 4:
				method.addParameter(0, new Parameter(pojoType, "record"));
				method.addParameter(1, new Parameter(pojoCriteriaType, "example"));
				return "record, example";
			default:
				break;
		}
		return null;
	}

	/**
	 * 导入需要的类
	 */
	private void addControllerImport(TopLevelClass topLevelClass) {
		topLevelClass.addImportedType(listType);
		topLevelClass.addImportedType(serviceType);
		topLevelClass.addImportedType(pojoType);
		topLevelClass.addImportedType(pageResultType);
		topLevelClass.addImportedType(baseResultType);
		topLevelClass.addImportedType(requestMethodType);
		topLevelClass.addImportedType(controllerAnnotationType);
		topLevelClass.addImportedType(responseBodyAnnotationType);
		topLevelClass.addImportedType(localOrRemoteServiceAnnotationType);
		topLevelClass.addImportedType(requestMappingAnnotationType);
	}

	private String getServiceShort() {
		return toLowerCase(serviceType.getShortName()) + ".";
	}

	private String getDaoShort() {
		return toLowerCase(daoType.getShortName()) + ".";
	}

	@Override
	public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
		if (!enableValidator)
			return null;
		String table = introspectedTable.getBaseRecordType();
		String tableName = table.replaceAll(this.pojoUrl + ".", "");
		String fileName = tableName + "Controller-validator.xml";
		Document document = new Document();
		XmlElement root = new XmlElement("validator");
		root.addAttribute(new Attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		root.addAttribute(new Attribute("xmlns", XmlConstants.ANYWIDE_CONTROLLER_XMLNS));
		if (this.schemaLocation != null && !"".equals(this.schemaLocation)) {
			root.addAttribute(new Attribute("xsi:schemaLocation",
					XmlConstants.ANYWIDE_CONTROLLER_XMLNS + " " + this.schemaLocation));
		} else {
			root.addAttribute(new Attribute("xsi:schemaLocation",
					XmlConstants.ANYWIDE_CONTROLLER_XMLNS
							+ "  https://cdn.jsdelivr.net/gh/srchen1987/dawdler-series-xsd@main/controller-validator.xsd"));
		}
		XmlElement vfs = new XmlElement("validator-fields");
		for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
			XmlElement vf = new XmlElement("validator-field");
			vf.addAttribute(new Attribute("name", column.getJavaProperty()));
			String remarks = column.getRemarks() == null ? "" : column.getRemarks();
			String rule = validateRule(column);
			String remarkRule = null;
			if (!remarks.equals("")) {
				remarkRule = remarkValidateRule(remarks);
			}

			if (remarkRule != null) {
				remarks = remarks.substring(0, remarks.lastIndexOf("{"));
				if (rule.endsWith("&")) {
					rule += remarkRule;
				} else {
					rule += "&" + remarkRule;
				}
			}
			vf.addAttribute(new Attribute("explain", remarks));
			vf.addElement(new TextElement("<![CDATA[" + rule + "]]>"));
			vfs.addElement(vf);
		}
		root.addElement(vfs);

		XmlElement vfgs = new XmlElement("validator-fields-groups");
		XmlElement vf = new XmlElement("validator-fields-group");
		vf.addAttribute(new Attribute("id", toLowerCase(tableName)));
		if (introspectedTable.hasPrimaryKeyColumns()) {
			for (IntrospectedColumn column : introspectedTable.getBaseColumns()) {
				XmlElement v = new XmlElement("validator");
				v.addAttribute(new Attribute("ref", column.getJavaProperty()));
				vf.addElement(v);
			}
		} else {
			boolean noFirst = false;
			for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
				if (!noFirst) {
					noFirst = true;
					continue;
				}
				XmlElement v = new XmlElement("validator");
				v.addAttribute(new Attribute("ref", column.getJavaProperty()));
				vf.addElement(v);
			}
		}
		vfgs.addElement(vf);
		root.addElement(vfgs);
		XmlElement vms = new XmlElement("validator-mappings");
		createValidatorMapping(vms, introspectedTable, tableName);

		root.addElement(vms);
		document.setRootElement(root);

		GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileName, controllerPack, webProject, false,
				context.getXmlFormatter());

		List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
		answer.add(gxf);

		return answer;
	}

	public static String validateRule(IntrospectedColumn column) {
		StringBuilder sb = new StringBuilder();
		int num = column.getJdbcType();
		boolean isNullable = column.isNullable();
		if (!isNullable) {
			sb.append("notEmpty&");
		}
		int length = column.getLength();
		switch (num) {
			case -7:// BIT
			case -6:// TINYINT
			case 5:// SMALLINT
			case 4:// INTEGER
			case -5:// BIGINT
				sb.append("number");
				break;
			case 6:// FLOAT
			case 7:// REAL
			case 8:// DOUBLE
			case 2:// NUMERIC
			case 3:// DECIMAL
				sb.append("realNumber");
				break;
			case 1:// CHAR
			case 12:// VARCHAR
			case -1:// LONGVARCHAR
				sb.append("maxSize:" + length);
				break;
			case 91:// DATE
				break;
			case 92:// TIME
				break;
			case 93:// TIMESTAMP
				break;
			case -2:// BINARY
				break;
			case -3:// VARBINARY
				break;
			case -4:// LONGVARBINARY
				break;
			case 0:// NULL
				break;
			case 1111:// OTHER
				break;
			case 2000:// JAVA_OBJECT
				break;
			case 2001:// DISTINCT
				break;
			case 2002:// STRUCT
				break;
			case 2003:// ARRAY
				break;
			case 2004:// BLOB
				break;
			case 2005:// CLOB
				break;
			case 2006:// REF
				break;
			case 70:// DATALINK
				break;
			case 16:// BOOLEAN
				break;
			case -8:// ROWID
				break;
			case -15:// NCHAR
			case -9:// NVARCHAR
			case -16:// LONGNVARCHAR
				sb.append("maxSize:" + length);
				break;
			case 2011:// NCLOB
				break;
			case 2009:// SQLXML
				break;
		}
		return sb.toString();
	}

	private void createValidatorMapping(XmlElement vms, String name, IntrospectedTable introspectedTable,
			String tableName, boolean isPrimaryKey, boolean enableRefgid) {
		XmlElement vm = new XmlElement("validator-mapping");
		vm.addAttribute(new Attribute("name", name));
		if (isPrimaryKey) {
			if (introspectedTable.getPrimaryKeyColumns().size() > 0) {
				for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
					XmlElement v = new XmlElement("validator");
					v.addAttribute(new Attribute("ref", column.getJavaProperty()));
					vm.addElement(v);
				}
			} else {
				XmlElement v = new XmlElement("validator");
				v.addAttribute(new Attribute("ref", introspectedTable.getAllColumns().get(0).getJavaProperty()));
				vm.addElement(v);
			}
		}
		if (enableRefgid) {
			XmlElement v = new XmlElement("validator");
			v.addAttribute(new Attribute("refgid", toLowerCase(tableName)));
			vm.addElement(v);
		}
		vms.addElement(vm);
	}

	private void createValidatorMapping(XmlElement vms, IntrospectedTable introspectedTable, String tableName) {
		if (this.enableInsert)
			createValidatorMapping(vms, prefix + toLowerCase(tableName) + prefix + "insert", introspectedTable,
					tableName, false, true);
		if (this.enableUpdate)
			createValidatorMapping(vms, prefix + toLowerCase(tableName) + prefix + "update", introspectedTable,
					tableName, true, true);
		if (this.enableDelete)
			createValidatorMapping(vms, prefix + toLowerCase(tableName) + prefix + "delete", introspectedTable,
					tableName, true, false);
	}

	/*
	 * ==================================生成service==================================
	 * =======
	 */
	/**
	 * 添加接口
	 *
	 * @param tableName
	 * @param files
	 */
	private void addService(Interface _interface, IntrospectedTable introspectedTable, String tableName,
			List<GeneratedJavaFile> files) {
		_interface.setVisibility(JavaVisibility.PUBLIC);
		_interface.addAnnotation("@Service(\"" + chanelGroupId + "\")");
		Method method = null;
		if (this.enableInfo) {
			method = selectByPrimaryKey(introspectedTable, tableName);
			method.getBodyLines().clear();
			method.setAbstract(true);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			_interface.addMethod(method);
		}
		if (this.enableList) {
			method = selectPageList(introspectedTable, tableName);
			method.getBodyLines().clear();
			method.setAbstract(true);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			_interface.addMethod(method);
		}
		if (this.enableUpdate) {
			method = getOtherInteger("updateByPrimaryKeySelective", introspectedTable, tableName, 1);
			method.getBodyLines().clear();
			method.setAbstract(true);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			_interface.addMethod(method);
		}
		if (this.enableInsert) {
			method = getOtherInsertBoolean("insertSelective", introspectedTable, tableName);
			method.getBodyLines().clear();
			method.setAbstract(true);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			_interface.addMethod(method);
		}
		if (this.enableDelete) {
			method = getOtherInteger("deleteByPrimaryKey", introspectedTable, tableName, 2);
			method.getBodyLines().clear();
			method.setAbstract(true);
			context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
			_interface.addMethod(method);
		}
		GeneratedJavaFile file = new GeneratedJavaFile(_interface, apiProject, context.getJavaFormatter());

		files.add(file);
	}

	/**
	 * 添加实现类
	 *
	 * @param introspectedTable
	 * @param tableName
	 * @param files
	 */
	private void addServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName,
			List<GeneratedJavaFile> files) {
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		// 设置实现的接口
		topLevelClass.addSuperInterface(serviceType);
		// 添加引用dao
		addField(topLevelClass, daoType, "@Repository");
		// 添加方法
		Method method = null;
		boolean isPrimaryKey = !introspectedTable.getPrimaryKeyColumns().isEmpty();
		if (this.enableInfo && isPrimaryKey) {
			method = selectByPrimaryKey(introspectedTable, tableName);
			method.addAnnotation("@Override");
			method.addAnnotation("@DBTransaction(mode=MODE.readOnly)");
			topLevelClass.addMethod(method);
		}
		if (this.enableList) {
			method = selectPageList(introspectedTable, tableName);
			method.addAnnotation("@Override");
			method.addAnnotation("@DBTransaction(mode=MODE.readOnly)");
			topLevelClass.addMethod(method);
		}
		if (this.enableUpdate && isPrimaryKey) {
			method = getOtherInteger("updateByPrimaryKeySelective", introspectedTable, tableName, 1);
			method.addAnnotation("@Override");
			method.addAnnotation("@DBTransaction(mode=MODE.forceReadOnWrite)");
			topLevelClass.addMethod(method);
		}
		if (this.enableInsert) {
			method = getOtherInsertBoolean("insertSelective", introspectedTable, tableName);
			method.addAnnotation("@Override");
			method.addAnnotation("@DBTransaction(mode=MODE.forceReadOnWrite)");
			topLevelClass.addMethod(method);
		}
		if (this.enableDelete && isPrimaryKey) {
			method = getOtherInteger("deleteByPrimaryKey", introspectedTable, tableName, 2);
			method.addAnnotation("@Override");
			method.addAnnotation("@DBTransaction(mode=MODE.forceReadOnWrite)");
			topLevelClass.addMethod(method);
		}

		// 生成文件
		GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, serviceProject, context.getJavaFormatter());
		files.add(file);
	}

	private Method selectPageList(IntrospectedTable introspectedTable, String tableName) {
		String lowerFirstName = lowerFirst(tableName);
		Method method = new Method("selectPageList");
		method.addParameter(0, new Parameter(pojoType, lowerFirstName));
		method.addParameter(1, new Parameter(FullyQualifiedJavaType.getIntegerInstance(), "pageOn"));
		method.addParameter(2, new Parameter(FullyQualifiedJavaType.getIntegerInstance(), "row"));
		method.setReturnType(pageResultType);
		method.setVisibility(JavaVisibility.PUBLIC);
		String listName = lowerFirstName + "List";
		StringBuilder sb = new StringBuilder();
		sb.append("int rowCount = ");
		sb.append(getDaoShort());
		sb.append("selectPageListCount");
		sb.append("(");
		sb.append(lowerFirstName);
		sb.append(");");
		sb.append("\n        ");
		sb.append("Page page = new Page(pageOn, row);");
		sb.append("\n        ");
		sb.append("page.setRowCountAndCompute(rowCount);");
		sb.append("\n        ");
		sb.append("List<");
		sb.append(pojoType.getShortName());
		sb.append("> ");
		sb.append(listName);
		sb.append(" = ");
		sb.append(getDaoShort());
		sb.append("selectPageList");
		sb.append("(");
		sb.append(lowerFirstName);
		sb.append(", page);");
		sb.append("\n        ");
		sb.append("return new PageResult<>(" + listName + ", page);");
		method.addBodyLine(sb.toString());
		return method;
	}

	private Method selectByPrimaryKey(IntrospectedTable introspectedTable, String tableName) {
		Method method = new Method("selectByPrimaryKey");
		method.setReturnType(baseResultType);
		if (!introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
				method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
			}
		} else {
			FullyQualifiedJavaType type = introspectedTable.getAllColumns().get(0).getFullyQualifiedJavaType();
			method.addParameter(new Parameter(type, introspectedTable.getAllColumns().get(0).getJavaProperty()));
		}
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		sb.append(pojoType.getShortName());
		sb.append(" ");
		sb.append(toLowerCase(pojoType.getShortName()));
		sb.append(" = ");
		sb.append(getDaoShort());
		sb.append("selectByPrimaryKey");
		sb.append("(");
		if (introspectedTable.getPrimaryKeyColumns().size() > 0) {
			if (introspectedTable.getPrimaryKeyColumns().size() == 1)
				sb.append(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
			else
				sb.append(toLowerCase(tableName)).append("Key");
		} else {
			sb.append(introspectedTable.getAllColumns().get(0).getJavaProperty());
		}
		sb.append(");");
		sb.append("\n        ");
		sb.append("return new BaseResult<>(" + toLowerCase(pojoType.getShortName()) + ");");
		method.addBodyLine(sb.toString());
		return method;
	}

	protected Method getOtherInteger(String methodName, IntrospectedTable introspectedTable, String tableName,
			int type) {
		Method method = new Method(methodName);
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		addParams(introspectedTable, method, tableName, type);
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		sb.append(type != 1 ? this.getPrimaryKeys(introspectedTable, tableName, type) : "");
		sb.append("return ");
		sb.append(getDaoShort());
		if (introspectedTable.hasBLOBColumns()
				&& (!"updateByPrimaryKeySelective".equals(methodName) && !"deleteByPrimaryKey".equals(methodName))) {
			sb.append(methodName + "WithoutBLOBs");
		} else {
			sb.append(methodName);
		}
		sb.append("(");
		sb.append(type != 1 ? this.getParameter(introspectedTable, tableName, null, type) : toLowerCase(tableName));
		sb.append(");");
		method.addBodyLine(sb.toString());
		return method;
	}

	private Method getOtherInsertBoolean(String methodName, IntrospectedTable introspectedTable, String tableName) {
		Method method = new Method(methodName);
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method.addParameter(new Parameter(pojoType, toLowerCase(tableName)));
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		sb.append("return ");
		sb.append(getDaoShort());
		sb.append(methodName);
		sb.append("(");
		sb.append(toLowerCase(tableName));
		sb.append(");");
		method.addBodyLine(sb.toString());
		return method;
	}

	/**
	 * BaseUsers to baseUsers
	 *
	 * @param tableName
	 * @return
	 */
	private String toLowerCase(String tableName) {
		StringBuilder sb = new StringBuilder(tableName);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString();
	}

	/**
	 * BaseUsers to baseUsers
	 *
	 * @param tableName
	 * @return
	 */
	private String toUpperCase(String tableName) {
		StringBuilder sb = new StringBuilder(tableName);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	/**
	 * 导入需要的类
	 */
	private void addServiceImport(Interface interfaces, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		interfaces.addImportedType(pojoType);
		interfaces.addImportedType(listType);
		interfaces.addImportedType(pageResultType);
		interfaces.addImportedType(baseResultType);
		interfaces.addImportedType(serviceAnnotationType);
		topLevelClass.addImportedType(daoType);
		topLevelClass.addImportedType(pageType);
		topLevelClass.addImportedType(pageResultType);
		topLevelClass.addImportedType(baseResultType);
		topLevelClass.addImportedType(serviceType);
		topLevelClass.addImportedType(pojoType);
		topLevelClass.addImportedType(listType);
		topLevelClass.addImportedType(dBTransactionAnnotationType);
		topLevelClass.addImportedType(dBTransactionMODEType);
		topLevelClass.addImportedType(repositoryAnnotationType);
		if (introspectedTable.getPrimaryKeyColumns().size() > 1)
			topLevelClass.addImportedType(new FullyQualifiedJavaType(pojoType.getFullyQualifiedName().concat("Key")));
	}

	private String isNull(String str) {
		if (str == null)
			return "";
		return str;
	}

	private StringBuilder getPrimaryKeys(IntrospectedTable introspectedTable, String tableName, int type) {
		StringBuilder sb = new StringBuilder();
		if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
			sb.append(tableName);
			sb.append("Key ");
			sb.append(toLowerCase(tableName));
			sb.append("Key");
			sb.append(" = new ");
			sb.append(tableName);
			sb.append("Key();");
			sb.append("\n        ");

			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				sb.append(toLowerCase(tableName));
				sb.append("Key");
				sb.append(".set");
				sb.append(toUpperCase(introspectedColumn.getJavaProperty()));
				sb.append("(");
				if (type != 1) {
					sb.append(introspectedColumn.getJavaProperty());
				} else {
					sb.append(toLowerCase(tableName));
					sb.append(".get");
					sb.append(toUpperCase(introspectedColumn.getJavaProperty()));
					sb.append("()");
				}
				sb.append(")");
				sb.append(";");
				sb.append("\n        ");
			}
		}
		return sb;
	}

	private StringBuilder getParameter(IntrospectedTable introspectedTable, String tableName, String variableName,
			int type) {
		StringBuilder sb = new StringBuilder();
		if (introspectedTable.getPrimaryKeyColumns().size() > 0) {
			if (introspectedTable.getPrimaryKeyColumns().size() == 1) {
				if (type == 2) {
					sb.append(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
				} else {
					sb.append(variableName);
					sb.append(".get");
					sb.append(toUpperCase(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty()));
					sb.append("()");
				}
			} else
				sb.append(toLowerCase(tableName)).append(type != 1 ? "Key" : "");
		} else {
			if (variableName != null) {
				sb.append(variableName);
				sb.append(".get");
				sb.append(toUpperCase(introspectedTable.getAllColumns().get(0).getJavaProperty()));
				sb.append("()");
			} else
				sb.append(toLowerCase(introspectedTable.getAllColumns().get(0).getJavaProperty()));
		}
		return sb;
	}

	public String remarkValidateRule(String remark) {
		if (remark == null) {
			return null;
		}
		int start = remark.lastIndexOf("{");
		int end = remark.lastIndexOf("}");
		if (start == -1 || start >= end) {
			return null;
		}
		return remark.substring(start + 1, end);
	}

}
