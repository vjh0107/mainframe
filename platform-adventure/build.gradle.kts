plugins {
    `java-library`
}

dependencies {
    api(project(":core"))

    compileOnlyApi("net.kyori:adventure-api")
}