package org.zaproxy.addon.naf.model

data class NafConfig(
    val nucleiEngineType: NucleiEngineType,
    val templateRootDir: String?,

    val sqlmapEngineType: SqlmapEngineType,
    val sqlmapApiUrl: String?,
    val sqlmapCSVLocation: String?,

    val commixEngineType: CommixEngineType,

    val tplmapEngineType: TplmapEngineType,

    val metasploitEngineType: MetasploitEngineType,
)

val home: String = System.getProperty("user.home")

internal val emptyConfig = NafConfig(
    NucleiEngineType.Native,
    "${home}/nuclei-templates",
    SqlmapEngineType.API,
    "http://127.0.0.1:8775/",
    "${home}/zap-naf/sqlmap/",
    CommixEngineType.DOCKER,
    TplmapEngineType.DOCKER,
    MetasploitEngineType.DOCKER,
)

fun emptyConfig() = emptyConfig


