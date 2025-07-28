import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// nur notwendig wegen launch4j! siehe https://github.com/TheBoegl/gradle-launch4j/issues/171
repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    // jewel + skiko
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.launch4j)
}

// -------------------
// Informations
// -------------------

val appVersionName = "0.0.1"
val appVersionCode = 1

val appName = "Toolbox Demo"
val androidNamespace = "com.michaelflisar.toolbox.demo"

// -------------------
// Setup
// -------------------

compose.resources {
    packageOfResClass = "com.michaelflisar.toolbox.demo.resources"
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    jvm()

    // -------
    // Sources
    // -------

    sourceSets {

        commonMain.dependencies {

            // resources
            implementation(compose.components.resources)

            // compose
            implementation(compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)
        }

        jvmMain.dependencies {

            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }

        }
    }
}

// -------------------
// Configurations
// -------------------

// windows configuration
compose.desktop {
    application {
        mainClass = "com.michaelflisar.toolbox.demo.MainKt"

        nativeDistributions {

            targetFormats(TargetFormat.Exe)

            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            packageName = appName // entspricht dem exe Name
            packageVersion = appVersionName
            description = "$appName - Build at ${now.format(formatter)}"
            copyright = "©${now.year} Michael Flisar. All rights reserved."
            vendor = "Michael Flisar"

            // https://github.com/JetBrains/compose-multiplatform/issues/1154
            // => suggestRuntimeModules task ausführen um zu prüfen, was man hier hinzufügen sollte
            // modules("java.instrument", "java.security.jgss", "java.sql", "java.xml.crypto", "jdk.unsupported")

            windows {
                iconFile.set(project.file("logo.ico"))
                //includeAllModules = true
            }

            buildTypes.release.proguard {

                version.set("7.7.0")

                // geht noch nicht, die config ist nicht korrekt
                isEnabled.set(false)

                configurationFiles.from(project.file("proguard-rules.pro"))
            }
        }
    }
}

// ---------
// FAT exe
// ---------

tasks.register<edu.sc.seis.launch4j.tasks.Launch4jLibraryTask>("launch4j") {

    mainClassName = "com.michaelflisar.toolbox.demo.MainKt"
    icon.set(project.file("logo.ico").absolutePath)
    setJarTask(project.tasks["flattenReleaseJars"])
    outfile = "$appName.exe"

    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    productName = appName
    version = appVersionName
    textVersion = appVersionName
    description = "$appName - Build at ${now.format(formatter)}"
    copyright = "©${now.year} Michael Flisar. All rights reserved."
    companyName = "Michael Flisar"

    doLast {

        val exe = outputs.files.find { it.extension == "exe" } ?:
            throw Exception("Launch4j output file not found!")

        println("")
        println("##############################")
        println("#          LAUNCH4J          #")
        println("##############################")
        println("")
        println("Executable wurde in folgendem Ordner erstellt:")
        println("file:///" + exe.parentFile.absolutePath.replace(" ", "%20").replace("\\", "/") + "")
        println("")

        //ProcessBuilder("explorer.exe", exe.parentFile.absolutePath).start()
    }

}