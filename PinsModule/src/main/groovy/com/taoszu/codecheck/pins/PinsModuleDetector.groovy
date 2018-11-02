package com.taoszu.codecheck.pins

import com.taoszu.codecheck.pins.entity.PinsModuleEntity
import com.taoszu.codecheck.pins.entity.PinsModuleFileEntity
import com.taoszu.codecheck.pins.tool.FileTool
import org.gradle.api.GradleException
import org.gradle.api.Project

class PinsModuleDetector {

    PinsModuleWareHouse pinsModuleWareHouse
    String lineSeparator = System.getProperty("line.separator")

    PinsModuleDetector(PinsModuleWareHouse pinsModuleWareHouse) {
        this.pinsModuleWareHouse = pinsModuleWareHouse
    }

    void scanModuleFile(PinsModuleEntity pinsModule) {
        traverseModuleJavaDir(pinsModule, FileTool.getJavaDir(pinsModule.pinsModuleDir), pinsModuleWareHouse.pinsModuleFileEntity)
    }

    void checkFile(Project project, PinsModuleEntity pinsModule) {
        String errorMessage = ""
        def resourcesPattern = /R.(dimen|drawable|color|string|style|id|mipmap|layout).[A-Za-z0-9_]+|import\s[A-Za-z0-9_.]+/

        PinsModuleFileEntity pinsModuleFileEntity = pinsModuleWareHouse.pinsModuleFileEntity

        pinsModuleFileEntity.fileMap.get(pinsModule.name).each {
            File file = it
            String currentModule = pinsModule.name

            String text = file.text
            List<String> textLines = text.readLines()
            def matcher = text =~ resourcesPattern
            def absolutePath = file.absolutePath

            while (matcher.find()) {
                def find = matcher.group()
                String fromModule, importPath

                if (find.startsWith("import")) {
                    importPath = find.substring(find.lastIndexOf(" ") + 1, find.length())
                    fromModule = pinsModuleFileEntity.getModuleFrom(importPath)
                }

                if (fromModule != null && currentModule != fromModule && !isReference(currentModule, fromModule)) {
                    List<Number> lines = textLines.findIndexValues { it.contains(find) }
                    lines.each {
                        def lineIndex = it.intValue()
                        def lineContext = textLines.get(lineIndex).trim()
                        if (lineContext.startsWith("//") || lineContext.startsWith("/*")) {
                            return
                        }

                        def message = absolutePath + ':' + (lineIndex + 1)
                        project.logger.error(message)
                        if(!errorMessage.contains(message)) {
                            message += lineSeparator
                            message += "- can't use [" + find + "] which from PinsModule '${fromModule}'."
                            message += lineSeparator
                            errorMessage += message
                        }
                    }
                }
            }
        }

        if (!errorMessage.isEmpty()) {
            throw new GradleException(errorMessage)
        }

    }

    /**
     * 遍历查找文件
     * @param pinsModule
     * @param dir
     * @param pinsModuleFileEntity
     */
   private void traverseModuleJavaDir(PinsModuleEntity pinsModule, File dir, PinsModuleFileEntity pinsModuleFileEntity) {
        dir.listFiles().each {
            if (it.isDirectory()) {
                traverseModuleJavaDir(pinsModule, it, pinsModuleFileEntity)
            } else {
                pinsModuleFileEntity.putFile(it, pinsModule)
            }
        }
    }

    boolean isReference(String currentModule, String fromModule) {
        Set<String> originalSet = new HashSet<>()
        originalSet.add(currentModule)
        return isReference(currentModule, fromModule, originalSet)
    }

    boolean isReference(String currentModule, String fromModule, Set<String> original) {
        Set<String> referenceSet = pinsModuleWareHouse.dependencyPinsModuleMap.get(currentModule)
        if (referenceSet == null){
            return false
        }
        if (referenceSet.contains(fromModule)) {
            return true
        }

        referenceSet.each {
            if (!original.contains(it)) {
                original.add(it)
            }
            if (isReference(it, fromModule, original)) {
                return true
            }
        }
        return false
    }

}