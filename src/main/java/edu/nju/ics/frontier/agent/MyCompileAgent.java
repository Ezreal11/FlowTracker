package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.listener.MyCompilationStatusListener;

/**
 * <strong>MyCompileAgent</strong> manages the compile listener,
 * which listens the compile activities occurred in IDE.
 */
public class MyCompileAgent implements Disposable {
    private Project project;
    private CompilationStatusListener compilationStatusListener;

    public MyCompileAgent(Project project) {
        this.project = project;
        this.compilationStatusListener = new MyCompilationStatusListener();
        // register the compile listener through CompilerManager
        CompilerManager.getInstance(this.project).addCompilationStatusListener(compilationStatusListener);
    }

    @Override
    public void dispose() {
        // unregister the compile listener through CompilerManager
        CompilerManager.getInstance(project).removeCompilationStatusListener(compilationStatusListener);
    }
}
