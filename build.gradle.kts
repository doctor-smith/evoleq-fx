import Config.Versions.kotlin
import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version Config.Versions.kotlin
    id ("com.github.hierynomus.license") version "0.15.0"
    `maven-publish`
    maven
    id ("com.jfrog.bintray") version "1.8.0"
    id("org.jetbrains.dokka") version "0.9.17"
}

group = Config.Project.group
version = Config.Project.version//"1.0.0-beta"//-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation (Config.Dependencies.kotlinStandardLibrary)
    implementation (Config.Dependencies.coroutines)
    implementation ( kotlin("reflect"))
    implementation ( Config.Dependencies.evoleq )

    testImplementation ( Config.Dependencies.junit )
    testImplementation ( Config.Dependencies.testfxcore )
    testImplementation ( Config.Dependencies.testfxjunit )
    testImplementation ( Config.Dependencies.testEvoleq )
    testImplementation ( Config.Dependencies.testEvoleqFX )
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8

}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks {

    val sourceSets: SourceSetContainer by project

    val sourcesJar by creating(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        classifier = "sources"
//        from(sourceSets["main"].allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
        classifier = "javadoc"
       // from(tasks["javadoc"])
    }

    val dokkaJar by creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        classifier = "javadoc"
        from(dokka)
    }
    
    artifacts {
        add("archives", sourcesJar)
        add("archives", dokkaJar)
    }

    val closeRunner by creating(Task::class){
        //classifier = "closeTestRunner"
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>< close runner")
    }

    

}

tasks.withType<Test>{
    (this as Task).finalizedBy(tasks.getByName("closeRunner"))

}
task("writeNewPom") {
    doLast {
        maven.pom {
            withGroovyBuilder {
                "project" {
                    // setProperty("inceptionYear", "2008")
                    "licenses" {
                        "license" {
                            setProperty("name", "The Apache Software License, Version 2.0")
                            setProperty("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                            setProperty("distribution", "repo")
                        }
                    }
                }
            }
        }.writeTo("$buildDir/pom.xml")
        //println("$buildDir")
    }
}

publishing {
    /*(publications) {
        "EvoleqFxPublication"(MavenPublication::class) {*/
    publications {
        create<MavenPublication>("mavenJava"){
            artifactId = Config.Project.artifactId
            groupId = Config.Project.group
            from (components["java"])

            artifact (tasks.getByName("sourcesJar")) {
                classifier = "sources"
            }

            artifact (tasks.getByName("javadocJar")) {
                classifier = "javadoc"
            }



            pom.withXml {
                val root = asNode()
                root.appendNode("description", "A declarative approach to application design based on the theory of dynamical systems")
                root.appendNode("name", Config.Project.artifactId)
                root.appendNode("url", "https://github.com/doctor-smith/evoleq.git")
                root.children().addAll(maven.pom().dependencies)
            }

            pom {
                developers{
                    developer{
                        id.set("drx")
                        name.set("Dr. Florian Schmidt")
                        email.set("schmidt@alpha-structure.com")
                    }
                }
            }

/*
            pom.withXml {
                asNode().appendNode("dependencies").let { depNode ->
                    configurations.compile.allDependencies.forEach {
                        depNode.appendNode("dependency").apply {
                            appendNode("groupId", it.group)
                            appendNode("artifactId", it.name)
                            appendNode("version", it.version)
                        }
                    }
                }
            }
*/
        }
    }
}




bintray {
    user = project.properties["bintray.user"] as String
    key = project.properties["bintray.key"] as String

    publish = true
    override = true

    //setPublications("EvoleqPublication")

    pkg (delegateClosureOf<BintrayExtension.PackageConfig>{
        repo = "maven"
        name = "evoleq"
        description = "A declarative approach to application design using the theory of dynamical systems"
        //userOrg = user
        vcsUrl = "https://bitbucket.org/dr-smith/evoleq-fx.git"
        setLabels("kotlin", "coroutine", "dynamical system", "recursive store", "evolution equation", "declarative", "functional")
        setLicenses("Apache-2.0")

        version (delegateClosureOf<BintrayExtension.VersionConfig>{
            name = Config.Project.version
            //desc = "build ${build.number}"
            //released  = Date(System.currentTimeMillis())
            gpg (delegateClosureOf<BintrayExtension.GpgConfig>{
                sign = true
            })
        })
    })

}
/*
fun MavenPom.addDependencies() = withXml {
    asNode().appendNode("dependencies").let { depNode ->
        configurations.compile.allDependencies.forEach {
            depNode.appendNode("dependency").apply {
                appendNode("groupId", it.group)
                appendNode("artifactId", it.name)
                appendNode("version", it.version)
            }
        }
    }
}
*/

