package com.taoszu.codecheck.pins

import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

class ScannerDetector {

    static Project project

    static boolean shouldScan(File file) {
        return file.absolutePath.replaceAll("\\\\", "/").contains("com/taoszu/codecheck/pins")
    }

    static void scanClass(File classFile) {
        try {
            new FileInputStream(classFile).withCloseable {
                ClassReader classReader = new ClassReader(it)
                ScanClassVisitor cv = new ScanClassVisitor()
                classReader.accept(cv, 0)
            }
        } catch (Exception e) {
            project.logger.error("error: " + e.message)
        }
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor() {
            super(Opcodes.ASM5)
        }


        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)

           // project.logger.error("class name" + name)
        }


    }

}