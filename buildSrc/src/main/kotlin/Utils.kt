import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-May-2020
 */
fun isInIdea() = System.getProperty("idea.active") == "true"

fun isInTravis() = System.getenv("TRAVIS") == "true"

fun getHostOsName(): String {
    val target = System.getProperty("os.name")
    if (target == "Linux") return "linux"
    if (target.startsWith("Windows")) return "windows"
    if (target.startsWith("Mac")) return "macos"
    return "unknown"
}

fun KotlinMultiplatformExtension.isRunningInIdea(block : KotlinMultiplatformExtension.() -> Unit) {
    if (isInIdea()) {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnLinux(block : KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "linux") {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnMacos(block : KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "macos") {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnWindows(block : KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "windows") {
        block(this)
    }
}
fun independentDependencyBlock(nativeDeps : KotlinDependencyHandler.() -> Unit) : KotlinDependencyHandler.() -> Unit {
    return nativeDeps
}

/**
 * On mac when two targets that have the same parent source set have cinterops defined, gradle creates a "common"
 * target task for that source set metadata, even though it's a native source set, to work around that, we create
 * an intermediary source set with the same set of dependancies
 */
fun NamedDomainObjectContainer<KotlinSourceSet>.createWorkaroundNativeMainSourceSet(name : String, nativeDeps : KotlinDependencyHandler.() -> Unit) : KotlinSourceSet {
    return create("${name}Workaround") {
        kotlin.srcDir("src/nativeMain/kotlin")
        dependencies {
            nativeDeps.invoke(this)
        }
    }

}