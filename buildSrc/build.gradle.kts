plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-scripting-jvm:1.3.21")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.21")
    compile("org.jetbrains.kotlin:kotlin-reflect:1.3.21")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}