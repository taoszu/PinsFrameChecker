package com.taoszu.codecheck.pins.entity

import com.taoszu.codecheck.pins.tool.FileTool
import org.gradle.api.Project


class PinsModuleFileEntity {
    Map<String, Set<String>> filePathMap = new HashMap<>()
    Map<String, Set<File>> fileMap = new HashMap<>()

    void putFile(File file, PinsModuleEntity pinsModule) {
        String moduleName = pinsModule.name

        Set<String> filePathSet = filePathMap.get(moduleName)
        if (filePathSet == null) {
            filePathSet = new HashSet<>()
            filePathMap.put(moduleName, filePathSet)
        }

        Set<File> fileSet = fileMap.get(moduleName)
        if (fileSet == null) {
            fileSet = new HashSet<>()
            fileMap.put(moduleName, fileSet)
        }

        fileSet.add(file)
        filePathSet.add(FileTool.filePathToImportPath(pinsModule, file.absolutePath))
    }

    String getModuleFrom(String classPath) {
        for (String key: filePathMap.keySet()) {
            Set<String> pathSet = filePathMap.get(key)
            if (pathSet.contains(classPath)) {
                return key
            }
        }
        return null
    }

    void printString(Project project) {
        project.logger.error('start   ----')
        filePathMap.keySet().forEach {
            project.logger.error(it + " --> " + filePathMap.get(it).toString())
        }

        fileMap.keySet().forEach {
            project.logger.error(it + " --> " + fileMap.get(it).toString())
        }
        project.logger.error('end   ----')
    }
}