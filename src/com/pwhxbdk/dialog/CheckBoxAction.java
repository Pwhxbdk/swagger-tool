package com.pwhxbdk.dialog;

import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBCheckBox;
import com.pwhxbdk.SwaggerTool;
import com.pwhxbdk.entity.DialogInfoWrapper;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: pwhxbdk
 * @Date: 2020/6/22 11:12
 */
public class CheckBoxAction {

    public static final String[] API_MODEL_PROPERTY_ATTRIBUTE_ARRAY = {"value","name","allowableValues","access","notes","dataType","required","position","hidden","example","accessMode","reference","allowEmptyValue","extensions"};
    public static final String[] API_MODEL_ATTRIBUTE_ARRAY = {"value","description","parent","discriminator","subTypes","reference"};

    private static Map<String,JCheckBox> checkBoxMap = new HashMap<>();
    public static String currentFieldName;

    public static JPanel init(String[] attributeArray) {
        JPanel jPanel = new JPanel(new GridLayout(3,2));
        jPanel.setBorder(IdeBorderFactory.createBorder(15));
        for (String filedName : attributeArray) {
            JBCheckBox jbCheckBox = new JBCheckBox(filedName);
            jbCheckBox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("currentFieldName:"+currentFieldName);
                    DialogInfoWrapper dialogInfoWrapper = Arena.fieldGlobalInfo.get(currentFieldName);
                    java.util.List<String> annotationAttrList = dialogInfoWrapper.getAnnotationAttr();
                    if (CollectionUtils.isEmpty(annotationAttrList)) {
                        annotationAttrList = new ArrayList<>();
                    }
                    if (jbCheckBox.isSelected()) {
                        annotationAttrList.add(jbCheckBox.getText());
                    } else {
                        annotationAttrList.remove(jbCheckBox.getText());
                    }
                    dialogInfoWrapper.setAnnotationAttr(annotationAttrList);
                    Arena.fieldGlobalInfo.put(currentFieldName,dialogInfoWrapper);
                    System.out.println(dialogInfoWrapper.toString());


                }
            });
            checkBoxMap.put(filedName,jbCheckBox);
            jPanel.add(jbCheckBox);
        }
        return jPanel;
    }

    public static void setCurrentField(String currentFieldName) {
        CheckBoxAction.currentFieldName = currentFieldName;
    }

    public static void selected(java.util.List<String> nameList) {
        if (CollectionUtils.isNotEmpty(nameList)) {
            for (String name : nameList) {
                JCheckBox jCheckBox = checkBoxMap.get(name);
                jCheckBox.setSelected(true);
            }
        }
    }

    public static void cancelAll() {
        for (JCheckBox jCheckBox : checkBoxMap.values()) {
            jCheckBox.setSelected(false);
        }
    }


}
