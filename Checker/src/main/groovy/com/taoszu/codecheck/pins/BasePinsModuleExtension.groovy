package com.taoszu.codecheck.pins

import org.gradle.api.GradleException
import org.gradle.api.Project

class BasePinsModuleExtension implements PinsModuleExtension {

    Project project
    OnPinsModuleListener onPinsModuleListener
    Set<PinsModule> includePinsModuleSet = new HashSet<>()

    BasePinsModuleExtension(Project project) {
        this.project = project
    }


    @Override
    void include(String... pinsModules) {
        for (String moduleName: pinsModules) {
            PinsModule pinsModule = buildPinsModule(moduleName)
            if (pinsModule == null) {
                throw new GradleException("can not find pinsModule '$moduleName' ")
            }
            addPinsModule(pinsModule)

            if (onPinsModuleListener != null) {
                onPinsModuleListener.addPinsModule(pinsModule)
            }
        }
    }

    private void addPinsModule(PinsModule pinsModule) {
        includePinsModuleSet.add(pinsModule)
    }

    PinsModule buildPinsModule(String path) {
        String[] pathList = removeTrailing(path).split(":")
        String srcFilePath = project.projectDir.path + File.separator + "src"
        File parentPinsModuleDir = new File(srcFilePath)

        for (int i = 0; i < pathList.length; i ++) {
            parentPinsModuleDir = new File(parentPinsModuleDir, pathList[i])
        }

        File pinsModuleDir = parentPinsModuleDir.canonicalFile
        if (!pinsModuleDir.exists()) {
            return null
        }

        String pinsModuleName = pinsModuleDir.absolutePath.replace(srcFilePath, "")
        if (File.separator == "\\") {
            pinsModuleName = pinsModuleName.replaceAll("\\\\", ":")
        } else {
            pinsModuleName = pinsModuleName.replaceAll("/", ":")
        }

        PinsModule pinsModule = new PinsModule()
        pinsModule.name = pinsModuleName
        pinsModule.pinsModuleDir = pinsModuleDir

        //project.logger.error("pins module " + pinsModule.toString())
        return pinsModule
    }


    private String removeTrailing(String pinsModule) {
        return pinsModule.startsWith(":") ? pinsModule.substring(1) : pinsModule
    }


    interface OnPinsModuleListener {
        void addPinsModule(PinsModule pinsModule)
    }

}