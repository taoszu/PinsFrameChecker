package com.taoszu.codecheck.pins.tool

import com.taoszu.codecheck.pins.entity.PinsModuleEntity
import org.gradle.api.Project

class FileTool {


    static String getSrcPath(Project project) {
        return project.projectDir.path + File.separator + "src"
    }

    static File getJavaDir(File dir) {
        return new File(dir, File.separator + "java")
    }

    static String getJavaDirPath(String dir) {
        return dir +  File.separator + "java" + File.separator
    }


    static File getPinsPropertiesFile(File pinsModuleDir) {
        return new File(pinsModuleDir, "pins.properties")
    }

    static String removeFileSuffixName(String filePath) {
        int pointIndex = filePath.lastIndexOf(".")
        if (pointIndex < 0) {
            return filePath
        } else {
            return filePath.substring(0, pointIndex)
        }
    }

    static String filePathToImportPath(PinsModuleEntity pinsModuleEntity, String path) {
        String javaDirPath = getJavaDirPath(pinsModuleEntity.pinsModuleDir.absolutePath)
        String importPath = path.replace(javaDirPath, "")
        importPath = removeFileSuffixName(importPath)
        importPath = importPath.replaceAll(File.separator, ".")
        return importPath
    }

}