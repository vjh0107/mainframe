plugins {
    `java-library`
}

dependencies {
    api(project(":core"))
    api(project(":platform-adventure"))

    compileOnlyApi("io.papermc.paper:paper-api")
    compileOnlyApi("kr.junhyung.papermc:paper-impl")

    compileOnly("org.springframework:spring-tx")
    compileOnly("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    compileOnly("org.springframework.boot:spring-boot-starter-micrometer-metrics")

    implementation("org.springframework.boot:spring-boot-starter-aspectj")

}