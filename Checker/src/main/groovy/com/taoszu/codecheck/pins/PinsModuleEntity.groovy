package com.taoszu.codecheck.pins


class PinsModuleEntity {
    String name
    File pinsModuleDir

    String toString() {
        return name + " " + pinsModuleDir.path
    }

    @Override
    boolean equals(Object obj) {
        PinsModuleEntity other = (PinsModuleEntity) obj
        return (this.name == other.name && this.pinsModuleDir == other.pinsModuleDir)

    }
}