package io.gitlab.arturbosch.detekt.config

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Tag
import org.intellij.detekt.api.PluginConfiguration

/**
 * @author Dmytro Primshyts
 */
@State(
    name = "DetektProjectConfiguration",
    storages = [(Storage(StoragePathMacros.WORKSPACE_FILE))]
)
class DetektConfigStorage : PersistentStateComponent<DetektConfigStorage> {

  @Tag
  var enableDetekt: Boolean = false

  @Tag
  var enableFormatting: Boolean = false

  @Tag
  var checkTestFiles: Boolean = false

  @Tag
  var treatAsError: Boolean = false

  @Tag
  var rulesPath: String = ""

  override fun getState(): DetektConfigStorage? = this

  override fun loadState(state: DetektConfigStorage) {
    this.enableDetekt = state.enableDetekt
    this.enableFormatting = state.enableFormatting
    this.checkTestFiles = state.checkTestFiles
    this.rulesPath = state.rulesPath
    this.treatAsError = state.treatAsError
  }

  companion object {

    /**
     * Get instance of [DetektConfigStorage] for given project.
     *
     * @param project the project
     */
    fun instance(project: Project): DetektConfigStorage =
        ServiceManager.getService(project, DetektConfigStorage::class.java)

    /**
     * Convert to [PluginConfiguration].
     *
     * @return instance of plugin configuration
     */
    fun toPluginConfiguration(project: Project) = with(instance(project)) {
      PluginConfiguration(
          enableDetekt = enableDetekt,
          enableFormatting = enableFormatting,
          checkTestFiles = checkTestFiles,
          treatAsError = treatAsError,
          rulesPath = rulesPath
      )
    }

  }

}
