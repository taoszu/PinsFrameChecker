package com.taoszu.codecheck.pins

class PinsModule {
    String name
    File pinsModuleDir

    String toString() {
        return name + " " + pinsModuleDir.path
    }
 }