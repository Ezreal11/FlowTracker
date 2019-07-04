package com.zzw.listener.edit;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class MyPsiTreeChangeListener implements PsiTreeChangeListener {
    private static final String TYPE = "Edit";

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {}

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {}

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {}

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {}

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {}

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {}

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        onPsiTreeChanged(System.currentTimeMillis(), "psi_child_added", event);
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        onPsiTreeChanged(System.currentTimeMillis(), "psi_child_removed", event);
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        onPsiTreeChanged(System.currentTimeMillis(), "psi_child_replaced", event);
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        onPsiTreeChanged(System.currentTimeMillis(), "psi_children_changed", event);
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        onPsiTreeChanged(System.currentTimeMillis(), "psi_child_moved", event);
    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
        onPsiTreeChanged(System.currentTimeMillis(), "psi_property_changed", event);
    }

    private void onPsiTreeChanged(long time, String when, PsiTreeChangeEvent event) {
        PsiElement oldParent = event.getOldParent();
        PsiElement oldChild = event.getOldChild();
        Object oldValue = event.getOldValue();

        PsiElement newParent = event.getNewParent();
        PsiElement newChild = event.getNewChild();
        Object newValue = event.getNewValue();

        if (!(StringUtil.isDifferent(oldParent, newParent) ||
                StringUtil.isDifferent(oldChild, newChild) ||
                StringUtil.isDifferent(oldValue, newValue))) {
            return;
        }

        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("op", StringUtil.toString(oldParent));
        e.addExtraData("oc", StringUtil.toString(oldChild));
        e.addExtraData("ov", StringUtil.toString(oldValue));
        e.addExtraData("np", StringUtil.toString(newParent));
        e.addExtraData("nc", StringUtil.toString(newChild));
        e.addExtraData("nv", StringUtil.toString(newValue));
        Persistence.getInstance().add(e);

//        System.out.println("\n" + time + "," + TYPE + "," + when);
//        if (StringUtil.isDifferent(oldParent, newParent)) {
//            System.out.println("op:" + oldParent + ",np:" + newParent);
//        }
//        if (StringUtil.isDifferent(oldChild, newChild)) {
//            System.out.println("oc:" + oldChild + ",nc:" + newChild);
//        }
//        if (StringUtil.isDifferent(oldValue, newValue)) {
//            System.out.println("ov:" + oldValue + ",nv:" + newValue);
//        }
    }
}
