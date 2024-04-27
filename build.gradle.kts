import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.yhs0602"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://jitpack.io")
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("io.github.skylot:jadx-core:1.5.0")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    // For archive extraction and murmur hash
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("com.kichik.pecoff4j:pecoff4j:0.4.1")
//    implementation("com.github.TomSmartBishop:facile-api:706bfa2")
    implementation("com.github.yhs0602:facile-api:-SNAPSHOT")
//    implementation("com.goncalossilva:murmurhash:0.4.0")
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.6")
    // https://mvnrepository.com/artifact/io.github.skylot/jadx-dex-input
    runtimeOnly("io.github.skylot:jadx-dex-input:1.5.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "reverser"
            packageVersion = "1.0.0"
        }
    }
}
