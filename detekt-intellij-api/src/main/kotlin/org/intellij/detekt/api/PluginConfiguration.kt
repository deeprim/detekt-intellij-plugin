package org.intellij.detekt.api

/**
 * Detekt Intellij Plugin configuration.
 */
data class PluginConfiguration(
    val enableDetekt: Boolean,
    val enableFormatting: Boolean,
    val checkTestFiles: Boolean,
    val treatAsError: Boolean,

    val rulesPath: String
)
