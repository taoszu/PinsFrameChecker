package com.taoszu.codecheck.pins

import org.gradle.api.Plugin
import org.gradle.api.Project

class CodeCheckerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {


        android.registerTransform(CodeTransform(project))
    }


}