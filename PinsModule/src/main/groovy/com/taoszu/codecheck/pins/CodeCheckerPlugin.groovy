package com.taoszu.codecheck.pins

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestPlugin
import com.taoszu.codecheck.pins.entity.PinsModuleEntity
import com.taoszu.codecheck.pins.extension.PinsModuleExtension
import com.taoszu.codecheck.pins.extension.PinsModuleExtensionInterface
import com.taoszu.codecheck.pins.tool.CommonTool
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class CodeCheckerPlugin implements Plugin<Project> {

    PinsModuleExtension pinsModuleExtension
    PinsModuleWareHouse pinsModuleWareHouse
    PinsModuleDetector pinsModuleDetector
    BaseExtension androidExtension

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)                                // AppPlugin
                && !project.plugins.hasPlugin(LibraryPlugin)                     // LibraryPlugin
                && !project.plugins.hasPlugin(TestPlugin)                        // TestPlugin
                && !project.plugins.hasPlugin("com.android.instantapp")       // InstantAppPlugin, added in 3.0
                && !project.plugins.hasPlugin("com.android.feature")          // FeaturePlugin, added in 3.0
                && !project.plugins.hasPlugin("com.android.dynamic-feature")) // DynamicFeaturePlugin, added in 3.2
        {
            throw new GradleException("require android plugin")
        }

        androidExtension = (BaseExtension) project.extensions.getByName("android")


        pinsModuleWareHouse = new PinsModuleWareHouse()
        pinsModuleDetector = new PinsModuleDetector(pinsModuleWareHouse)
        pinsModuleExtension = project.extensions.create(PinsModuleExtensionInterface, "pinsModule", PinsModuleExtension, project)
        pinsModuleExtension.onPinsModuleListener = new PinsModuleExtension.OnPinsModuleListener() {
            @Override
            void addPinsModule(PinsModuleEntity pinsModule) {
                pinsModuleWareHouse.addPinsModule(pinsModule)
            }
        }


        project.afterEvaluate {

            pinsModuleWareHouse.includePinsModuleMap.values().forEach {
                pinsModuleWareHouse.checkPinsProperties(it)
            }

            project.logger.error(pinsModuleWareHouse.dependencyPinsModuleMap.toString())

            androidExtension.buildTypes.each {
                String buildType = CommonTool.upperCase(it.name)

                String compileTaskName = "compile${buildType}JavaWithJavac"
                def compileTask = project.tasks.findByName(compileTaskName)
                if (compileTask != null) {
                    compileTask.doLast {
                        pinsModuleWareHouse.includePinsModuleMap.values().forEach {
                            PinsModuleEntity moduleEntity = it
                            pinsModuleDetector.scanModuleFile(moduleEntity)
                        }

                        pinsModuleWareHouse.includePinsModuleMap.values().forEach {
                            pinsModuleDetector.checkFile(project, it)
                        }
                    }
                }


            }


        }
    }


}