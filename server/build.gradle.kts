plugins {
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":api"))
    
    implementation("com.bugsnag:bugsnag:[3.0,4.0)")
    implementation("org.apache.logging.log4j:log4j-core:2.25.4")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.4")
    implementation("com.lmax:disruptor:3.4.4")
    implementation("org.jline:jline:3.30.6")
    implementation("org.jline:jline-terminal:3.30.6")
    implementation("org.jline:jline-terminal-jna:3.30.6")
    implementation("org.jline:jline-reader:3.30.6")
    implementation("net.minecrell:terminalconsoleappender:1.3.0") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
        exclude(group = "org.jline", module = "jline-reader")
        exclude(group = "org.jline", module = "jline-terminal-jna")
        exclude(group = "org.jline", module = "jline-terminal")
    }
    implementation("org.cloudburstmc.protocol:bedrock-connection:3.0.0.Beta12-SNAPSHOT")
    implementation("org.cloudburstmc.netty:netty-transport-raknet:1.0.0.CR3-20260421.213623-35")
    implementation("io.netty:netty-transport-native-epoll:4.1.101.Final:linux-x86_64")
    implementation("io.netty:netty-transport-native-kqueue:4.1.101.Final:osx-x86_64")
    implementation("com.nimbusds:nimbus-jose-jwt:9.37.4")
}

tasks.shadowJar {
    archiveFileName.set("Waterdog.jar")
    mergeServiceFiles()
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
    manifest {
        attributes["Main-Class"] = "dev.waterdog.waterdogpe.WaterdogPE"
        attributes["Multi-Release"] = "true"
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
