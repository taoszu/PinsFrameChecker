package com.taoszu.codecheck.pins.tool
import org.gradle.api.Project

class FileTool {


    static String getSrcPath(Project project) {
        return project.projectDir.path + File.separator + "src"
    }

    static File getPinsPropertiesFile(File pinsModuleDir) {
        return new File(pinsModuleDir, "pins.properties")
    }

}