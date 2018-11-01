package com.taoszu.codecheck.pins

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class CodeCheckerPlugin implements Plugin<Project> {

    BasePinsModuleExtension pinsModuleExtension

    Map<String, Set<String>> pinsModuleDependencyMap

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

        pinsModuleExtension = project.extensions.create(PinsModuleExtension, "pinsModule", BasePinsModuleExtension, project)
        pinsModuleExtension.onPinsModuleListener = new BasePinsModuleExtension.OnPinsModuleListener() {
            @Override
            void addPinsModule(PinsModule pinsModule) {

            }
        }


        project.afterEvaluate {
            pinsModuleDependencyMap = new HashMap<>()

            pinsModuleExtension.includePinsModuleSet.each {
                applyPinsModuleBuild(project, it)
            }

            project.logger.error(pinsModuleDependencyMap.toString())

            pinsModuleExtension.includePinsModuleSet.each {
                checkPinsModuleDependency(it)
            }
        }

    }

    def checkPinsModuleDependency(PinsModule pinsModule) {
        Set<String> dependencyList = pinsModuleDependencyMap.get(pinsModule.name)
        if (dependencyList == null) return

        for (String path : dependencyList) {
            checkPinsModuleDependency(pinsModule, path)
        }
    }

    def checkPinsModuleDependency(PinsModule pinsModule, String path) {
        PinsModule dependencyPinsModule = pinsModuleExtension.buildPinsModule(path)
        if (dependencyPinsModule == null) {
            throw new GradleException("can't find specified PinsModule '${path}', which is dependent by PinsModule '${pinsModule.name}'")
        }

        boolean include = false
        pinsModuleExtension.includePinsModuleSet.each {
            if (it.name == dependencyPinsModule.name) {
                include = true
            }
        }

        if (!include) {
            throw new GradleException("PinsModule '${pinsModule.name}' dependency PinsModule '${dependencyPinsModule.name}', but its not included.")
        }
    }

    def applyPinsModuleBuild(Project project, PinsModule pinsModule) {
        def pinsModuleBuild = new File(pinsModule.pinsModuleDir, "pins.properties")
        if (pinsModuleBuild.exists()) {

            Properties pinsProperties = new Properties()
            pinsProperties.load(new FileInputStream(pinsModuleBuild.absolutePath))

            String pinsConfig = pinsProperties.getProperty("pinsModule")
            if (pinsConfig == null || pinsConfig.isEmpty()) {
                return
            }

            String[] pinsArray = pinsConfig.split(",")
            pinsArray.each {
                pinsModuleDependencyHandler(pinsModule, it)
            }

        }
    }

    def pinsModuleDependencyHandler(PinsModule currentPinsModule, String path) {
        PinsModule pinsModule = pinsModuleExtension.buildPinsModule(path)
        if (pinsModule == null) {
            return
        }

        Set<String> dependencySet = pinsModuleDependencyMap.get(currentPinsModule.name)
        if (dependencySet == null) {
            dependencySet = new HashSet<>()
            dependencySet.add(pinsModule.name)
            pinsModuleDependencyMap.put(currentPinsModule.name, dependencySet)
        } else {
            dependencySet.add(pinsModule.name)
        }

    }

}