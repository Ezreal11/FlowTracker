package edu.nju.ics.frontier.listener;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyCompilationStatusListener</strong> implements <em>CompilationStatusListener</em>
 * to listen the compile activities.
 */
public class MyCompilationStatusListener extends MyListener implements CompilationStatusListener {
    public MyCompilationStatusListener(Persistence persistence) {
        super(persistence, "Compile");
    }

    /**
     * the specific data for the finished compile IntelliJEvent consists:
     * <ul>
     *     <li>aborted: true if the compile process is aborted (e.g., false)</li>
     *     <li>errors: number of the errors occurred in this compile (e.g., 0)</li>
     *     <li>warnings: number of the warnings occurred in this compile (e.g., 0)</li>
     * </ul>
     * @see CompilationStatusListener#compilationFinished(boolean, int, int, CompileContext)
     * @param aborted
     * @param errors
     * @param warnings
     * @param compileContext
     */
    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        IntelliJEvent e = new IntelliJEvent(System.currentTimeMillis(), getType(), "finished");
        e.addExtraData("aborted", String.valueOf(aborted));
        e.addExtraData("errors", String.valueOf(errors));
        e.addExtraData("warnings", String.valueOf(warnings));
        getPersistence().add(e);
    }

    /**
     * the specific data for the auto-finished compile IntelliJEvent consists:
     * <ul>
     *     <li>errors: number of the errors occurred in this compile (e.g., 0)</li>
     *     <li>warnings: number of the warnings occurred in this compile (e.g., 0)</li>
     * </ul>
     * @see CompilationStatusListener#automakeCompilationFinished(int, int, CompileContext)
     * @param errors
     * @param warnings
     * @param compileContext
     */
    @Override
    public void automakeCompilationFinished(int errors, int warnings, @NotNull CompileContext compileContext) {
        IntelliJEvent e = new IntelliJEvent(System.currentTimeMillis(), getType(), "auto_finished");
        e.addExtraData("errors", String.valueOf(errors));
        e.addExtraData("warnings", String.valueOf(warnings));
        getPersistence().add(e);
    }

    /**
     * the specific data for the auto-finished compile IntelliJEvent consists:
     * <ul>
     *     <li>root: MD5-coded directory to place the compiling files</li>
     *     <li>path: MD5-coded path of the compiling files</li>
     * </ul>
     * @see CompilationStatusListener#fileGenerated(String, String)
     * @param outputRoot
     * @param relativePath
     */
    @Override
    public void fileGenerated(@NotNull String outputRoot, @NotNull String relativePath) {
        IntelliJEvent e = new IntelliJEvent(System.currentTimeMillis(), getType(), "file_generated");
        e.addExtraData("root", StringUtil.md5Encode(outputRoot));
        e.addExtraData("path", StringUtil.md5Encode(relativePath));
        getPersistence().add(e);
    }
}
