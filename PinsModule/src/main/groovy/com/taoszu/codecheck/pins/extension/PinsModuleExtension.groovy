package com.taoszu.codecheck.pins.extension

import com.taoszu.codecheck.pins.PinsModuleEntity
import com.taoszu.codecheck.pins.PinsModuleWareHouse
import com.taoszu.codecheck.pins.tool.CommonTool
import com.taoszu.codecheck.pins.tool.FileTool
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * pins gradle扩展
 * 用于读取 pinsModule 配置的pins选项
 * 然后生成pins工程的配置
 */
class PinsModuleExtension implements PinsModuleExtensionInterface {

    Project project
    OnPinsModuleListener onPinsModuleListener

    PinsModuleExtension(Project project) {
        this.project = project
    }


    @Override
    void include(String... pinsModuleName) {
        for (String moduleName: pinsModuleName) {
            if (moduleName == null || moduleName.isEmpty()) {
                throw new GradleException("pinsModule name can not be empty")
            }

            PinsModuleEntity pinsModule = buildPinsModule(CommonTool.formatModuleName(moduleName))
            if (pinsModule == null) {
                throw new GradleException("can not find pinsModule '$moduleName' ")
            }
            if (onPinsModuleListener != null) {
                onPinsModuleListener.addPinsModule(pinsModule)
            }
        }
    }

    private PinsModuleEntity buildPinsModule(String moduleName) {
        String srcPath = FileTool.getSrcPath(project)
        File pinsModuleDir = new File(new File(srcPath), moduleName)
        if (!pinsModuleDir.exists()) {
            return null
        }

        PinsModuleEntity pinsModule = new PinsModuleEntity()
        pinsModule.name = moduleName
        pinsModule.pinsModuleDir = pinsModuleDir
        return pinsModule
    }

    interface OnPinsModuleListener {
        void addPinsModule(PinsModuleEntity pinsModule)
    }

}