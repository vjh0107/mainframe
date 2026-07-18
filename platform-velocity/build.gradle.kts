plugins {
    `java-library`
}

dependencies {
    api(project(":core"))
    api(project(":platform-adventure"))

    implementation("org.springframework.boot:spring-boot-starter-aspectj")

    compileOnlyApi("com.velocitypowered:velocity-api")
    compileOnly("org.springframework.cloud:spring-cloud-commons")
    compileOnly("kr.junhyung.pluginjar:velocity-loader:1.3.0-SNAPSHOT")
}
