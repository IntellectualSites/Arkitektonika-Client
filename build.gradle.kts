import com.diffplug.gradle.spotless.SpotlessPlugin
import java.net.URI

plugins {
    java
    `maven-publish`
    signing

    id("com.diffplug.spotless") version "6.25.0"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    implementation("com.intellectualsites.http:HTTP4J:1.3")
    implementation("org.jetbrains:annotations:26.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    compileOnly("com.google.code.gson:gson:2.12.1")
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
version = "2.1.4-SNAPSHOT"

spotless {
    java {
        licenseHeaderFile(rootProject.file("LICENSE"))
        target("**/*.java")
    }
}

tasks {
    compileJava {
        options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
        options.compilerArgs.add("-Xlint:all")
        for (disabledLint in arrayOf("processing", "path", "fallthrough", "serial"))
            options.compilerArgs.add("-Xlint:$disabledLint")
        options.isDeprecation = true
        options.encoding = "UTF-8"
    }

    javadoc {
        title = project.name + " " + project.version
        val opt = options as StandardJavadocDocletOptions
        opt.addStringOption("Xdoclint:none", "-quiet")
        opt.tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )
        opt.addBooleanOption("html5", true)
        opt.links("https://javadoc.io/doc/org.jetbrains/annotations/24.1.0/")
        opt.isLinkSource = true
        opt.isUse = true
        opt.encoding("UTF-8")
        opt.keyWords()
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

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {

                name.set(project.name + " " + project.version)
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
            }
        }
    }
}

nexusPublishing {
    this.repositories {
        sonatype {
            nexusUrl.set(URI.create("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
