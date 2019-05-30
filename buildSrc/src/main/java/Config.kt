object Config {

    object Project {
        val group = "org.drx"
        // val version = "1.0.0" ParallelFx
        // val version = "1.0.1" AsyncFx
        // val version = "1.0.2" // AppManager, Launcher, FxComponent,
        val version = "1.1.0" // Configuration Flow and Dsl improvements
        val artifactId = "evoleq-fx"
    }

    object GitHub {
        val url = "https://github.com/doctor-smith/${Project.artifactId}.git"
    }

    object Versions {
        val kotlin = "1.3.30"
        val coroutines = "1.2.0"
        val evoleq = "1.0.3"

        val testEvoleq = "1.0.0"
        val testEvoleqFx = "1.0.0"
        val junit = "4.12"
        val testfxcore = "4.0.15-alpha"
        val testfxjunit = "4.0.15-alpha"
    }

    object Dependencies {
        val kotlinStandardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        val junit = "junit:junit:${Versions.junit}"
        val evoleq = "org.drx:evoleq:${Versions.evoleq}"

        val testEvoleq = "org.drx:test-evoleq:${Versions.testEvoleq}"
        val testEvoleqFX = "org.drx:test-evoleq-fx:${Versions.testEvoleq}"
        val testfxcore = "org.testfx:testfx-core:${Versions.testfxcore}"
        val testfxjunit = "org.testfx:testfx-junit:${Versions.testfxjunit}"
    }


}
