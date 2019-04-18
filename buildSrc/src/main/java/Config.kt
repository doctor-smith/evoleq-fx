object Config {

    object Project {
        val group = "org.drx"
        // val version = "1.0.0" ParallelFx
        // val version = "1.0.1" AsyncFx
        // val version = "1.0.2" // AppManager, Launcher, FxComponent,
        val version = "1.1.0" // Configuration Flow and Dsl improvements
        val artifactId = "evoleq-fx"
    }

    object Versions {

        val kotlin = "1.3.30"
        val coroutines = "1.2.0"



        val junit = "4.12"



    }

    object Dependencies {
        val kotlinStandardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"


        val junit = "junit:junit:${Versions.junit}"
    }


}
