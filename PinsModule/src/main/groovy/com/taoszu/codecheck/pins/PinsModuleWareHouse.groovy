package com.taoszu.codecheck.pins

import com.taoszu.codecheck.pins.tool.CommonTool
import com.taoszu.codecheck.pins.tool.FileTool
import org.gradle.api.GradleException

class PinsModuleWareHouse {

    /**
     * 格式为 p_pay : .../src/p_pay
     */
    Map<String, PinsModuleEntity> includePinsModuleMap = new HashMap<>()

    /**
     * 格式为 p_pay : [p_pay, p_shop]
     */
    Map<String, Set<String>> dependencyPinsModuleMap = new HashMap<>()


    PinsModuleWareHouse() {

    }

    void addPinsModule(PinsModuleEntity pinsModule) {
        includePinsModuleMap.put(pinsModule.name, pinsModule)
    }

    PinsModuleEntity getPinsModule(String moduleName) {
        return includePinsModuleMap.get(CommonTool.formatModuleName(moduleName))
    }

    /**
     * 检查pins配置
     * @param pinsModule
     */
    void checkPinsProperties(PinsModuleEntity pinsModule) {
        File pinsPropertiesFile = FileTool.getPinsPropertiesFile(pinsModule.pinsModuleDir)
        if (!pinsPropertiesFile.exists()) {
            return
        }

        Properties pinsProperties = new Properties()
        pinsProperties.load(new FileInputStream(pinsPropertiesFile.absolutePath))
        String pinsConfig = pinsProperties.getProperty("pinsModule")
        if (pinsConfig == null || pinsConfig.isEmpty()) {
            return
        }

        String[] pinsArray = pinsConfig.split(",")
        pinsArray.each {
            genPinsDependency(pinsModule, it)
        }
    }

    private void genPinsDependency(PinsModuleEntity currentPinsModule, String pinsModuleName) {
        PinsModuleEntity dependPinsModule = getPinsModule(pinsModuleName)
        if (dependPinsModule == null) {
            throw new GradleException("can not find specified pinsModule '${pinsModuleName}' in pinsModule{} with gradle, which is dependent by '${currentPinsModule.name}'")
        }

        Set<String> dependencySet = dependencyPinsModuleMap.get(currentPinsModule.name)
        if (dependencySet == null) {
            dependencySet = new HashSet<>()
            dependencySet.add(currentPinsModule.name)
            dependencyPinsModuleMap.put(currentPinsModule.name, dependencySet)
        } else {
            dependencySet.add(currentPinsModule.name)
        }

    }

}