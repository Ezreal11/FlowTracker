package com.zzw.listener.execute;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import org.jetbrains.annotations.NotNull;

public class MyCompilationStatusListener implements CompilationStatusListener {
    private static final String TYPE = "Compile";

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        IntelliJEvent e = new IntelliJEvent(System.currentTimeMillis(), TYPE, "finished");
        e.addExtraData("aborted", String.valueOf(aborted));
        e.addExtraData("errors", String.valueOf(errors));
        e.addExtraData("warnings", String.valueOf(warnings));
        Persistence.getInstance().add(e);
    }

    @Override
    public void automakeCompilationFinished(int errors, int warnings, @NotNull CompileContext compileContext) {
        IntelliJEvent e = new IntelliJEvent(System.currentTimeMillis(), TYPE, "auto_finished");
        e.addExtraData("errors", String.valueOf(errors));
        e.addExtraData("warnings", String.valueOf(warnings));
        Persistence.getInstance().add(e);
    }

    @Override
    public void fileGenerated(@NotNull String outputRoot, @NotNull String relativePath) {
        IntelliJEvent e = new IntelliJEvent(System.currentTimeMillis(), TYPE, "file_generated");
        e.addExtraData("root", outputRoot);
        e.addExtraData("path", relativePath);
        Persistence.getInstance().add(e);
    }
}
