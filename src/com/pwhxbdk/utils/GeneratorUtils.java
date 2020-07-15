package com.pwhxbdk.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: pwhxbdk
 * @Date: 2020/7/15 15:32
 */
public class GeneratorUtils {

    private static final String MAPPING_VALUE = "value";
    private static final String MAPPING_METHOD = "method";
    private static final String REQUEST_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String POST_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PostMapping";
    private static final String GET_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.GetMapping";
    private static final String DELETE_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.DeleteMapping";
    private static final String PATCH_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PatchMapping";
    private static final String PUT_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PutMapping";
    private static final String REQUEST_PARAM_TEXT = "org.springframework.web.bind.annotation.RequestParam";
    private static final String REQUEST_HEADER_TEXT = "org.springframework.web.bind.annotation.RequestHeader";
    private static final String PATH_VARIABLE_TEXT = "org.springframework.web.bind.annotation.PathVariable";
    private static final String REQUEST_BODY_TEXT = "org.springframework.web.bind.annotation.RequestBody";

    private final Project project;
    private final PsiFile psiFile;
    private final PsiElementFactory elementFactory;
    private final String selectionText;

    public GeneratorUtils(Project project, PsiFile psiFile, PsiElementFactory elementFactory, String selectionText) {
        this.project = project;
        this.psiFile = psiFile;
        this.elementFactory = elementFactory;
        this.selectionText = selectionText;
    }

    public void doGenerate() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            boolean selection = false;
            if (StringUtils.isNotEmpty(selectionText)) {
                selection = true;
            }
            // 遍历当前对象的所有属性
            for (PsiElement psiElement : psiFile.getChildren()) {
                if (psiElement instanceof PsiClass){
                    PsiClass psiClass = (PsiClass) psiElement;
                    boolean isController = this.isController(psiClass);
                    if (selection) {
                        this.generateSelection(psiClass, selectionText, isController);
                        break;
                    }
                    // 获取注释
                    this.generateClassAnnotation(psiClass,isController);
                    if (isController) {
                        // 类方法列表
                        PsiMethod[] methods = psiClass.getMethods();
                        for (PsiMethod psiMethod : methods) {
                            this.generateMethodAnnotation(psiMethod);
                        }
                    } else {
                        // 类属性列表
                        PsiField[] field = psiClass.getAllFields();
                        for (PsiField psiField : field) {
                            this.generateFieldAnnotation(psiField);
                        }
                    }
                }
            }
        });
    }

    /**
     * 写入到文件
     * @param name 注解名
     * @param qualifiedName 注解全包名
     * @param annotationText 生成注解文本
     * @param psiModifierListOwner 当前写入对象
     */
    private void doWrite(String name, String qualifiedName, String annotationText, PsiModifierListOwner psiModifierListOwner) {
        PsiAnnotation psiAnnotationDeclare = elementFactory.createAnnotationFromText(annotationText, psiModifierListOwner);
        final PsiNameValuePair[] attributes = psiAnnotationDeclare.getParameterList().getAttributes();
        PsiAnnotation existAnnotation = psiModifierListOwner.getModifierList().findAnnotation(qualifiedName);
        if (existAnnotation != null) {
            existAnnotation.delete();
        }
        addImport(elementFactory, psiFile, name);
        PsiAnnotation psiAnnotation = psiModifierListOwner.getModifierList().addAnnotation(name);
        for (PsiNameValuePair pair : attributes) {
            psiAnnotation.setDeclaredAttributeValue(pair.getName(), pair.getValue());
        }
    }

    /**
     * 类是否为controller
     * @param psiClass 类元素
     * @return boolean
     */
    private void generateSelection(PsiClass psiClass, String selectionText, boolean isController) {
        if (Objects.equals(selectionText, psiClass.getName())) {
            this.generateClassAnnotation(psiClass,isController);
        }
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod psiMethod : methods) {
            if (Objects.equals(selectionText, psiMethod.getName())) {
                this.generateMethodAnnotation(psiMethod);
                return;
            }
        }
        PsiField[] field = psiClass.getAllFields();
        for (PsiField psiField : field) {
            if (Objects.equals(selectionText, psiField.getNameIdentifier().getText())) {
                this.generateFieldAnnotation(psiField);
                return;
            }
        }
    }

    /**
     * 类是否为controller
     * @param psiClass 类元素
     * @return boolean
     */
    private boolean isController(PsiClass psiClass) {
        PsiAnnotation[] psiAnnotations = psiClass.getModifierList().getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            String controllerAnnotation = "org.springframework.stereotype.Controller";
            String restControllerAnnotation = "org.springframework.web.bind.annotation.RestController";
            if (controllerAnnotation.equals(psiAnnotation.getQualifiedName())
                    || restControllerAnnotation.equals(psiAnnotation.getQualifiedName())) {
                // controller
                return true;
            }
        }
        return false;
    }

    /**
     * 获取RequestMapping注解属性
     * @param psiAnnotations 注解元素数组
     * @param attributeName 属性名
     * @return String 属性值
     */
    private String getMappingAttribute(PsiAnnotation[] psiAnnotations, String attributeName) {
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            switch (Objects.requireNonNull(psiAnnotation.getQualifiedName())) {
                case REQUEST_MAPPING_ANNOTATION:
                    return getAttribute(psiAnnotation, attributeName);
                case POST_MAPPING_ANNOTATION:
                    return "POST";
                case GET_MAPPING_ANNOTATION:
                    return "GET";
                case DELETE_MAPPING_ANNOTATION:
                    return "DELETE";
                case PATCH_MAPPING_ANNOTATION:
                    return "PATCH";
                case PUT_MAPPING_ANNOTATION:
                    return "PUT";
                default:break;
            }
        }
        return "";
    }

    /**
     * 获取注解属性
     * @param psiAnnotation 注解全路径
     * @param attributeName 注解属性名
     * @return 属性值
     */
    private String getAttribute(PsiAnnotation psiAnnotation, String attributeName) {
        if (Objects.isNull(psiAnnotation)) {
            return "\"\"";
        }
        PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findDeclaredAttributeValue(attributeName);
        if (Objects.isNull(psiAnnotationMemberValue)) {
            return "\"\"";
        }
        return psiAnnotationMemberValue.getText();
    }

    /**
     * 生成类注解
     * @param psiClass 类元素
     * @param isController 是否为controller
     */
    private void generateClassAnnotation(PsiClass psiClass, boolean isController){
        PsiComment classComment = null;
        for (PsiElement tmpEle : psiClass.getChildren()) {
            if (tmpEle instanceof PsiComment){
                classComment = (PsiComment) tmpEle;
                // 注释的内容
                String tmpText = classComment.getText();
                String commentDesc = CommentUtils.getCommentDesc(tmpText);
                String annotationFromText;
                String annotation;
                String qualifiedName;
                if (isController) {
                    annotation = "Api";
                    qualifiedName = "io.swagger.annotations.Api";
                    String fieldValue = this.getMappingAttribute(psiClass.getModifierList().getAnnotations(),MAPPING_VALUE);
                    annotationFromText = String.format("@%s(value = %s, tags = {\"%s\"})",annotation,fieldValue,commentDesc);
                } else {
                    annotation = "ApiModel";
                    qualifiedName = "io.swagger.annotations.ApiModel";
                    annotationFromText = String.format("@%s(value = \"%s\")", annotation, commentDesc);
                }
                this.doWrite(annotation, qualifiedName, annotationFromText, psiClass);
            }
        }
        if (Objects.isNull(classComment)) {
            String annotationFromText;
            String annotation;
            String qualifiedName;
            if (isController) {
                annotation = "Api";
                qualifiedName = "io.swagger.annotations.Api";
                String fieldValue = this.getMappingAttribute(psiClass.getModifierList().getAnnotations(),MAPPING_VALUE);
                annotationFromText = String.format("@%s(value = %s)",annotation,fieldValue);
            } else {
                annotation = "ApiModel";
                qualifiedName = "io.swagger.annotations.ApiModel";
                annotationFromText = String.format("@%s", annotation);
            }
            this.doWrite(annotation, qualifiedName, annotationFromText, psiClass);
        }
    }

    /**
     * 生成方法注解
     * @param psiMethod 类方法元素
     */
    private void generateMethodAnnotation(PsiMethod psiMethod){
        PsiAnnotation[] psiAnnotations = psiMethod.getModifierList().getAnnotations();
        String methodValue = this.getMappingAttribute(psiAnnotations,MAPPING_METHOD);
        if (StringUtils.isNotEmpty(methodValue)) {
            methodValue = methodValue.substring(methodValue.indexOf(".")+1);
        }
        // 如果存在注解，获取注解原本的value和notes内容
        PsiAnnotation apiOperationExist = psiMethod.getModifierList().findAnnotation("io.swagger.annotations.ApiOperation");
        String apiOperationAttrValue = this.getAttribute(apiOperationExist,"value");
        String apiOperationAttrNotes = this.getAttribute(apiOperationExist,"notes");
        String apiOperationAnnotationText = String.format("@ApiOperation(value = %s, notes = %s,httpMethod = \"%s\")", apiOperationAttrValue, apiOperationAttrNotes, methodValue);
        String apiImplicitParamsAnnotationText = null;
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        List<String> apiImplicitParamList = new ArrayList<>(psiParameters.length);
        for (PsiParameter psiParameter : psiParameters) {
            PsiType psiType = psiParameter.getType();
            String dataType = CommentUtils.getDataType(psiType.getCanonicalText(), psiType);
            if (StringUtils.isEmpty(dataType)) {
                continue;
            }
            String paramType = "query";
            if (Objects.equals(dataType,"file")) {
                paramType = "form";
            }

            for (PsiAnnotation psiAnnotation : psiParameter.getModifierList().getAnnotations()) {
                if (StringUtils.isEmpty(psiAnnotation.getQualifiedName())) {
                    break;
                }
                switch (psiAnnotation.getQualifiedName()) {
                    case REQUEST_HEADER_TEXT:
                        paramType = "header";
                        break;
                    case REQUEST_PARAM_TEXT:
                        paramType = "query";
                        break;
                    case PATH_VARIABLE_TEXT:
                        paramType = "path";
                        break;
                    case REQUEST_BODY_TEXT:
                        paramType = "body";
                        break;
                    default:
                        break;
                }
            }
            String apiImplicitParamText =
                    String.format("@ApiImplicitParam(paramType = \"%s\", dataType = \"%s\", name = \"%s\", value = \"\")",paramType,dataType,psiParameter.getNameIdentifier().getText());
            apiImplicitParamList.add(apiImplicitParamText);
        }
        if (apiImplicitParamList.size() != 0) {
            apiImplicitParamsAnnotationText = apiImplicitParamList.stream().collect(Collectors.joining(",\n", "@ApiImplicitParams({\n", "\n})"));
        }

        this.doWrite("ApiOperation", "io.swagger.annotations.ApiOperation", apiOperationAnnotationText, psiMethod);
        this.doWrite("ApiImplicitParams", "io.swagger.annotations.ApiImplicitParams", apiImplicitParamsAnnotationText,psiMethod);
        WriteCommandAction.runWriteCommandAction(project, () -> addImport(elementFactory, psiFile, "ApiImplicitParam"));
    }

    /**
     * 生成属性注解
     * @param psiField 类属性元素
     */
    private void generateFieldAnnotation(PsiField psiField){
        PsiComment classComment = null;
        for (PsiElement tmpEle : psiField.getChildren()) {
            if (tmpEle instanceof PsiComment) {
                classComment = (PsiComment) tmpEle;
                // 注释的内容
                String tmpText = classComment.getText();
                String commentDesc = CommentUtils.getCommentDesc(tmpText);
                String apiModelPropertyText = String.format("@ApiModelProperty(value=\"%s\")",commentDesc);
                this.doWrite("ApiModelProperty", "io.swagger.annotations.ApiModelProperty", apiModelPropertyText, psiField);
            }
        }
        if (Objects.isNull(classComment)) {
            this.doWrite("ApiModelProperty", "io.swagger.annotations.ApiModelProperty", "@ApiModelProperty(\"\")", psiField);
        }
    }

    /**
     * 导入类依赖
     * @param elementFactory 元素Factory
     * @param file 当前文件对象
     * @param className 类名
     */
    private void addImport(PsiElementFactory elementFactory, PsiFile file, String className) {
        if (!(file instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) file;
        // 获取所有导入的包
        final PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        // 待导入类有多个同名类或没有时 让用户自行处理
        if (psiClasses.length != 1) {
            return;
        }
        PsiClass waiteImportClass = psiClasses[0];
        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
            String impQualifiedName = is.getImportReference().getQualifiedName();
            if (waiteImportClass.getQualifiedName().equals(impQualifiedName)) {
                // 已经导入
                return;
            }
        }
        importList.add(elementFactory.createImportStatement(waiteImportClass));
    }
}