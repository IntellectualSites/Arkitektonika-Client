import com.diffplug.gradle.spotless.SpotlessPlugin
import com.vanniktech.maven.publish.SonatypeHost
import java.net.URI

plugins {
    java
    signing

    id("com.diffplug.spotless") version "7.0.4"
    id("com.vanniktech.maven.publish") version "0.32.0"
}

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation("com.intellectualsites.http:HTTP4J:1.8")
    implementation("org.jetbrains:annotations:26.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
    compileOnly("com.google.code.gson:gson:2.13.1")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.16")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.compileJava.configure {
    options.release.set(8)
}

configurations.all {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
}

group = "com.intellectualsites.arkitektonika"
version = "2.1.5-SNAPSHOT"

spotless {
    java {
        licenseHeaderFile(rootProject.file("LICENSE"))
        target("**/*.java")
    }
}

tasks {

    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.links("https://javadoc.io/doc/org.jetbrains/annotations/26.0.2/")
        opt.noTimestamp()
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}

signing {
    if (!project.hasProperty("skip.signing") && !version.toString().endsWith("-SNAPSHOT")) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        signing.isRequired
        sign(publishing.publications)
    }
}

mavenPublishing {
    coordinates(
        groupId = "$group",
        artifactId = project.name,
        version = "${project.version}",
    )

    pom {
        name.set(project.name)
        description.set("Java client for the Arkitektonika API")
        url.set("https://github.com/IntellectualSites/Arkitektonika-Client")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("Citymonstret")
                name.set("Alexander Söderberg")
                organization.set("IntellectualSites")
                organizationUrl.set("https://github.com/IntellectualSites")
            }
            developer {
                id.set("NotMyFault")
                name.set("Alexander Brandes")
                organization.set("IntellectualSites")
                organizationUrl.set("https://github.com/IntellectualSites")
                email.set("contact(at)notmyfault.dev")

            }
        }

        scm {
            url.set("https://github.com/IntellectualSites/Arkitektonika-Client")
            connection.set("scm:git:https://github.com/IntellectualSites/Arkitektonika-Client.git")
            developerConnection.set("scm:git:git@github.com:IntellectualSites/Arkitektonika-Client.git")
            tag.set("${project.version}")
        }

        issueManagement{
            system.set("GitHub")
            url.set("https://github.com/IntellectualSites/Arkitektonika-Client/issues")
        }

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    }
}
