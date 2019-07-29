package edu.nju.ics.frontier.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;

import java.awt.*;

public class ProjectUtil {
    public static Project getActiveProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects == null || projects.length <= 0) {
            return null;
        }

        Project activeProject = null;
        for (Project project : projects) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive()) {
                activeProject = project;
            }
        }
        return activeProject;
    }
}
