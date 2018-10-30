package com.taoszu.codecheck.pins

import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ScannerDetector {

    static Project project

    static boolean shouldScan(File file) {
        return file.absolutePath.replaceAll("\\\\", "/").contains("com/taoszu/codecheck/pins/p_")
    }

    static void scanClass(File classFile) {
        try {
            new FileInputStream(classFile).withCloseable {
                ClassReader classReader = new ClassReader(it)

                project.logger.error("reader : " + classReader.className)

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
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
            return new ScanMethodVisitor(methodVisitor)
        }
    }

    static class  ScanMethodVisitor extends MethodVisitor {
        ScanMethodVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM5, classVisitor)
        }


        @Override
        void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc)
            project.logger.error("field: " + owner + " -> name: " + name + " -> desc: " + desc)
        }

        @Override
        void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
            project.logger.error("method: " + owner + " -> name: " + name + " -> desc: " + desc)
        }


    }



}