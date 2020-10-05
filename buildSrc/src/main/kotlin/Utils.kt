import org.gradle.api.NamedDomainObjectContainer
import org.gradle.nativeplatform.platform.internal.Architectures
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-May-2020
 */
fun isInIdea() = System.getProperty("idea.active") == "true"

fun isInGitlabCi() = System.getenv("GITLAB_CI") == "true"

fun getProjectPath() : String {
    val path = System.getProperty("PROJECT_PATH")
    return path
}

fun getHostOsName(): String {
    val target = System.getProperty("os.name")
    if (target == "Linux") return "linux"
    if (target.startsWith("Windows")) return "windows"
    if (target.startsWith("Mac")) return "macos"
    return "unknown"
}

fun getHostArchitecture(): String {
    val architecture = System.getProperty("os.arch")
    DefaultNativePlatform.getCurrentArchitecture()
    println("Arch: $architecture")
    val resolvedArch = Architectures.forInput(architecture).name
    println("Resolved arch: $resolvedArch")
    return resolvedArch
}

fun KotlinMultiplatformExtension.isRunningInIdea(block: KotlinMultiplatformExtension.() -> Unit) {
    if (isInIdea()) {
        block(this)
    }
}

fun KotlinMultiplatformExtension.isNotRunningInIdea(block: KotlinMultiplatformExtension.() -> Unit) {
    if (!isInIdea()) {
        block(this)
    }
}

fun KotlinMultiplatformExtension.isRunningInGitlabCi(block: KotlinMultiplatformExtension.() -> Unit) {
    if (isInGitlabCi()) {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnLinuxx86_64(block: KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "linux" && getHostArchitecture() == "x86-64") {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnLinuxArm64(block: KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "linux" && getHostArchitecture() == "aarch64") {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnLinuxArm32(block: KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "linux" && getHostArchitecture() == "arm-v7") {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnMacos(block: KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "macos") {
        block(this)
    }
}

fun KotlinMultiplatformExtension.runningOnWindows(block: KotlinMultiplatformExtension.() -> Unit) {
    if (getHostOsName() == "windows") {
        block(this)
    }
}

fun independentDependencyBlock(nativeDeps: KotlinDependencyHandler.() -> Unit): KotlinDependencyHandler.() -> Unit {
    return nativeDeps
}

/**
 * On mac when two targets that have the same parent source set have cinterops defined, gradle creates a "common"
 * target task for that source set metadata, even though it's a native source set, to work around that, we create
 * an intermediary source set with the same set of dependancies
 *
 */
fun NamedDomainObjectContainer<KotlinSourceSet>.createWorkaroundNativeMainSourceSet(
    name: String,
    nativeDeps: KotlinDependencyHandler.() -> Unit
): KotlinSourceSet {

    return create("${name}Workaround") {
        if (!isInIdea()) {
            kotlin.srcDir("src/nativeMain")
            dependencies {
                nativeDeps.invoke(this)
            }
        }
    }

}