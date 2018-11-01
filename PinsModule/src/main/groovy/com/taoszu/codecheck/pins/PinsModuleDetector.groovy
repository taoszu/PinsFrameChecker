package com.taoszu.codecheck.pins

import com.taoszu.codecheck.pins.tool.FileTool
import org.gradle.api.Project

class PinsModuleDetector {

    PinsModuleDetector() {


    }

    void checkFile(Project project, PinsModuleEntity pinsModule) {
        List<File> moduleFileList = new ArrayList<>()
        traverseModuleJavaDir(FileTool.getJavaDir(pinsModule.pinsModuleDir), moduleFileList)

        moduleFileList.forEach {
            String text = it.text
            List<String> textLines = text.readLines()
            def matcher = (text =~ resourcesPattern)
            def absolutePath = it.absoluxtePath
            def pinsModuleName = pinsModule.name


        }
    }


    void traverseModuleJavaDir(File dir, List<File> moduleFileList) {
        dir.listFiles().each {
            if (it.isDirectory()) {
                traverseModuleJavaDir(it, moduleFileList)
            } else {
                moduleFileList.add(it)
            }
        }
    }

}