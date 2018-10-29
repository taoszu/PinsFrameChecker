package com.taoszu.codecheck.pins

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

class CodeTransform extends Transform {

    Project project

    CodeTransform(Project project) {
        this.project = project
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    String getName() {
        return "CodeChecker"
    }

    @Override
    boolean isIncremental() {
        return false
    }


    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        ScannerDetector.project = project

           transformInvocation.inputs.each { TransformInput input ->
                   if (!input.directoryInputs.empty) {
                       input.directoryInputs.each { DirectoryInput directoryInput  ->
                           directoryInput.file.eachFileRecurse { File file ->
                               if (file.isFile() && ScannerDetector.shouldScan(file)) {
                                   ScannerDetector.scanClass(file)
                               }
                           }
                       }
                   }
           }

    }


}