object Dependency {
    object Kotlin {
        const val Version = "1.9.0"
    }
    object Paper {
        const val Version = "1.20.1"
        const val APIVersion = "1.20"
    }
    private const val KotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Kotlin.Version}"
    private const val JDA = "net.dv8tion:JDA:5.0.0-beta.12"
    private object Kommand {
        const val Version = "3.1.6"
    }
    val Dependencies = arrayListOf(
        KotlinStdlib,
        "io.github.monun:kommand-api:${Kommand.Version}",
        "com.google.code.gson:gson:2.8.8",
        JDA
    )
    val Libraries = arrayListOf(
        KotlinStdlib,
        "io.github.monun:kommand-core:${Kommand.Version}",
        JDA
    )
}