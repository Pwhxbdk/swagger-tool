package com.pwhxbdk;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.pwhxbdk.utils.GeneratorUtils;

/**
 * @author pwhxbdk
 * @date 2020/4/5
 */
public class SwaggerTool extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // 获取当前的project对象
        Project project = anActionEvent.getProject();
        // 获取当前文件对象
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        assert editor != null;
        assert project != null;
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        String selectionText = editor.getSelectionModel().getSelectedText();
        new GeneratorUtils(project, psiFile, psiClass, selectionText).doGenerate();
    }

}
