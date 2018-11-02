package com.taoszu.codecheck.pins.tool

import org.gradle.api.Project

class CommonTool {

    static String formatModuleName(String moduleName) {
        return moduleName.replace(":", "")
    }

    static String upperCase(String str) {
        char[] ch = str.toCharArray()
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] -= 32
        }
        return String.valueOf(ch)
    }


}