package com.pwhxbdk.entity;

import com.intellij.psi.PsiMember;

import java.util.List;

/**
 * @Author: wangluming
 * @Date: 2020/6/23 18:12
 */
public class DialogInfoWrapper {
    private String memberName;
    private PsiMember psiMember;
    private List<String> annotationAttr;
    private String editAnnotation;

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public PsiMember getPsiMember() {
        return psiMember;
    }

    public void setPsiMember(PsiMember psiMember) {
        this.psiMember = psiMember;
    }

    public List<String> getAnnotationAttr() {
        return annotationAttr;
    }

    public void setAnnotationAttr(List<String> annotationAttr) {
        this.annotationAttr = annotationAttr;
    }

    public String getEditAnnotation() {
        return editAnnotation;
    }

    public void setEditAnnotation(String editAnnotation) {
        this.editAnnotation = editAnnotation;
    }

    @Override
    public String toString() {
        return "DialogInfoWrapper{" +
                "memberName='" + memberName + '\'' +
                ", psiMember=" + psiMember +
                ", annotationAttr=" + annotationAttr +
                ", editAnnotation='" + editAnnotation + '\'' +
                '}';
    }
}
