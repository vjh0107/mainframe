plugins {
    `java-library`
}

dependencies {
    api(project(":core"))
    api(project(":platform-adventure"))

    compileOnly("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")

    compileOnlyApi("io.papermc.paper:paper-api")
    compileOnlyApi("kr.junhyung.papermc:paper-impl")
}