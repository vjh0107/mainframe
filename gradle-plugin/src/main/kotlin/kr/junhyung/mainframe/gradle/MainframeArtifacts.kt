package kr.junhyung.mainframe.gradle

object MainframeArtifacts {

    val group: String by lazy {
        MainframeArtifacts::class.java.`package`.implementationVendor
            ?: error("Could not locate the mainframe group")
    }

    val version: String by lazy {
        MainframeArtifacts::class.java.`package`.implementationVersion
            ?: error("Could not locate the mainframe version")
    }

    val bom: String get() = "$group:mainframe-bom:$version"

    val platformPaper: String get() = "$group:mainframe-platform-paper:$version"

    val platformVelocity: String get() = "$group:mainframe-platform-velocity:$version"
}
