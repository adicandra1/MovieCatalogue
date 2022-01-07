
@Suppress("unused")
object Config {
    const val compileSdkVersion = 28
    const val minSdkVersion = 19
    const val targetSdkVersion = 28
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

}

object Versions {
    const val kotlin = "1.3.50"
    const val kotlin_coroutines = "1.1.1"
    const val kotlin_ktx_jetpack = "1.1.0"

    const val lifecycle_viewmodel = "2.1.0-beta01"
    const val lifecycle_ext = "2.1.0"
    const val annotationProcessor_lifecycle_ext = "1.1.1"
    const val room = "2.1.0"
    const val work_manager = "2.2.0"

    const val appcompat = "1.1.0"
    const val constraintlayout = "1.1.3"
    const val recyclerview = "1.0.0"
    const val androidmaterial = "1.0.0"
    const val chipcloud = "2.2.1"
    const val glide = "4.9.0"
    const val gson = "2.8.5"

    const val lombok = "1.18.8"

    const val junit = "4.12"
    const val mockito = "2.23.0"

    const val testrunner = "1.2.0"
    const val junit_ext = "1.1.1"
    const val espresso = "3.2.0"
}

@Suppress("unused")
object Dependencies {
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val kotlin_ktx_jetpack = "androidx.core:core-ktx:${Versions.kotlin_ktx_jetpack}"
    const val kotlin_coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlin_coroutines}"

    //viewModelScope coroutines dependency
    const val lifecycle_viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_viewmodel}"

    const val lifecycle_ext = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle_ext}"
    const val annotationProsessor_lifecycle_ext = "android.arch.lifecycle:common-java8:${Versions.annotationProcessor_lifecycle_ext}"

    //room
    const val room_runtime = "androidx.room:room-runtime:${Versions.room}"
    const val room_ktx = "androidx.room:room-ktx:${Versions.room}"
    const val kapt_room_compiler = "androidx.room:room-compiler:${Versions.room}"

    //workmanager
    const val work_manager = "androidx.work:work-runtime-ktx:${Versions.work_manager}"

    const val appcompat =  "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val recyclerview =  "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
    const val androidmaterial = "com.google.android.material:material:${Versions.androidmaterial}"
    const val chipcloud = "com.github.adroitandroid:ChipCloud:${Versions.chipcloud}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    const val lombok = "org.projectlombok:lombok:${Versions.lombok}"
    const val lombok_annotationProcessor = "org.projectlombok:lombok:${Versions.lombok}"

    const val junit = "junit:junit:${Versions.junit}"
    const val mockito_core = "org.mockito:mockito-core:${Versions.mockito}"
    const val mockito_inline = "org.mockito:mockito-inline:${Versions.mockito}"

    const val testrunner = "androidx.test:runner:${Versions.testrunner}"
    const val junit_ext = "androidx.test.ext:junit:${Versions.junit_ext}"
    const val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}