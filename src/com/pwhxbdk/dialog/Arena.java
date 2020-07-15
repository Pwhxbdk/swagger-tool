package com.pwhxbdk.dialog;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.*;
import com.intellij.ui.*;
import com.intellij.ui.components.*;
import com.pwhxbdk.entity.DialogInfoWrapper;
import com.pwhxbdk.utils.CommentUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: pwhxbdk
 * @Date: 2020/6/19 15:15
 */
public class Arena extends DialogWrapper {

    private final LabeledComponent<JPanel> listComponent;
    private final LabeledComponent<EditorTextField> textComponent;
    private LabeledComponent<JPanel> fieldAnnotationComponent;
    private LabeledComponent<JPanel> methodAnnotationComponent;
    private final JList<PsiMember> memberList;
    private EditorTextField editorTextField;
    public static Map<String, DialogInfoWrapper> fieldGlobalInfo;

    public Arena(final PsiClass ownerClass, Project project) {
        super(ownerClass.getProject());
        this.setTitle("Select Method or Field for Generate");
        initAnnotation(ownerClass);



        CollectionListModel<PsiMember> list = new CollectionListModel(ownerClass.getFields());
        list.add(0, ownerClass);
        (this.memberList = new JBList(list)).setCellRenderer(new DefaultPsiElementCellRenderer());
        memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberList.setSelectedIndex(0);
        CheckBoxAction.setCurrentField(memberList.getModel().getElementAt(0).getName());
        memberList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] indices = memberList.getSelectedIndices();
                // 获取选项数据的 ListModel
                ListModel<PsiMember> listModel = memberList.getModel();
                // 输出选中的选项
                for (int index : indices) {
                    if (listModel.getElementAt(index) instanceof PsiClass){
                        fieldAnnotationComponent.setVisible(false);
                        methodAnnotationComponent.setVisible(true);
                    } else if (!fieldAnnotationComponent.isVisible()) {
                        fieldAnnotationComponent.setVisible(true);
                        methodAnnotationComponent.setVisible(false);
                    }

                    String name = listModel.getElementAt(index).getName();
                    CheckBoxAction.setCurrentField(name);
                    CheckBoxAction.cancelAll();
                    CheckBoxAction.selected(fieldGlobalInfo.get(name).getAnnotationAttr());

                    String text = fieldGlobalInfo.get(name).getEditAnnotation();
                    editorTextField.setText(text);
                }
            }
        });
        final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.memberList);
        decorator.disableAddAction();
        decorator.disableRemoveAction();
        decorator.disableUpDownActions();
        final JPanel panel = decorator.createPanel();
        this.listComponent = LabeledComponent.create(panel, "Select Fields or Method");


        editorTextField = new EditorTextField(project, StdFileTypes.PLAIN_TEXT);
        editorTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fieldGlobalInfo.get(CheckBoxAction.currentFieldName).setEditAnnotation(editorTextField.getText());
                System.out.println("editorTextField.getText():"+editorTextField.getText());
            }
        });
        editorTextField.setSize(100,100);
        editorTextField.setOneLineMode(false);
        editorTextField.setBorder(IdeBorderFactory.createBorder(15));
        editorTextField.setText(fieldGlobalInfo.get(list.getElementAt(0).getName()).getEditAnnotation() + "\n");
        this.textComponent = LabeledComponent.create(editorTextField, "Annotations");


        JPanel apiModelProperty = CheckBoxAction.init(CheckBoxAction.API_MODEL_PROPERTY_ATTRIBUTE_ARRAY);
        this.fieldAnnotationComponent = LabeledComponent.create(apiModelProperty, "Annotation Attributes");
        JPanel apiModel = CheckBoxAction.init(CheckBoxAction.API_MODEL_ATTRIBUTE_ARRAY);
        this.methodAnnotationComponent = LabeledComponent.create(apiModel, "Annotation Attributes");
        this.methodAnnotationComponent.setVisible(false);
        this.init();
    }



    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        JPanel displayAttrPanel = new JPanel(new CardLayout());
        displayAttrPanel.add(methodAnnotationComponent);
        displayAttrPanel.add(fieldAnnotationComponent);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(displayAttrPanel,BorderLayout.NORTH);
        centerPanel.add(this.textComponent,BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.add(this.listComponent, BorderLayout.NORTH);
        totalPanel.add(centerPanel, BorderLayout.CENTER);

        dialogPanel.add(totalPanel);
        return dialogPanel;
    }


    private void initAnnotation(PsiClass psiClass) {
        fieldGlobalInfo = new HashMap<>(psiClass.getAllFields().length + 1);
        String classCommonDesc = "";
        for (PsiElement tmpEle : psiClass.getChildren()) {
            if (tmpEle instanceof PsiComment){
                PsiComment classComment = (PsiComment) tmpEle;
                classCommonDesc = CommentUtils.getCommentDesc(classComment.getText());
            }
        }
        String classDesc = CommentUtils.getCommentDesc(classCommonDesc);
        String classAnnotation = String.format("@%s(value = \"%s\")", "ApiModel", classDesc);
        DialogInfoWrapper classInfo = new DialogInfoWrapper();
        classInfo.setEditAnnotation(classAnnotation);
        classInfo.setPsiMember(psiClass);
        fieldGlobalInfo.put(psiClass.getName(),classInfo);

        for (PsiField psiField : psiClass.getAllFields()) {
            String fieldCommonDesc = "";
            for (PsiElement tmpEle : psiField.getChildren()) {
                if (tmpEle instanceof PsiComment) {
                    PsiComment fieldComment = (PsiComment) tmpEle;
                    fieldCommonDesc = CommentUtils.getCommentDesc(fieldComment.getText());
                }
            }
            String annotation = String.format("@ApiModelProperty(value=\"%s\")", fieldCommonDesc);
            DialogInfoWrapper fieldInfo = new DialogInfoWrapper();
            fieldInfo.setEditAnnotation(annotation);
            fieldInfo.setPsiMember(psiField);
            fieldGlobalInfo.put(psiField.getName(),fieldInfo);
        }
    }

}
