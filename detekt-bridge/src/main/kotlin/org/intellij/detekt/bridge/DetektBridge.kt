package org.intellij.detekt.bridge

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.FileProcessorLocator
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.RuleSetLocator
import org.intellij.detekt.api.*
import java.nio.file.Paths
import java.util.concurrent.ForkJoinPool

/**
 * Class is used to access detekt functionality.
 * The bridge allows to omit actual detekt version and API difference between
 * various versions.
 */
class DetektBridge(
    private val input: String,
    private val pluginConfiguration: PluginConfiguration
) {

  private val processingSettings: ProcessingSettings = ProcessingSettings(
      inputPath = Paths.get(input),
      config = NoAutoCorrectConfig(CliArgs().apply {
        config = pluginConfiguration.rulesPath
      }.loadConfiguration()),
      executorService = ForkJoinPool.commonPool()
  )

  fun execute(): DiaDetektion {
    return createFacade().run().toDiaDetektion()
  }

  private fun createFacade(settings: ProcessingSettings = processingSettings,
                           configuration: PluginConfiguration = pluginConfiguration): DetektFacade {
    var providers = RuleSetLocator(settings).load().run {
      if (!configuration.enableFormatting) {
        filterNot { it.ruleSetId == "formatting" }
      } else {
        this
      }
    }

    val processors = FileProcessorLocator(settings).load()
    return DetektFacade.create(settings, providers, processors)
  }

  private fun Detektion.toDiaDetektion(): DiaDetektion {
    return DiaDetektion(
        findings = findings.map { entry ->
          entry.key to entry.value.map { finding -> finding.toDiaFinding() }
        }.toMap(),
        metrics = metrics.map { metric -> metric.toDiaMetric() },
        notifications = notifications.map { notification -> notification.toDiaNotification() }
    )
  }

  /**
   * Convert current [Finding] to [DiaIssue].
   *
   * @receiver [Finding]
   * @return dia finding
   */
  private fun Finding.toDiaFinding(): DiaFinding {
    return DiaFinding(
        id,
        issue.toDiaIssue(),
        references.map { reference -> reference.toDiaEntity() },
        message,
        messageOrDescription(),
        entity.toDiaEntity()
    )
  }

  /**
   * Convert current [Issue] to [DiaIssue].
   *
   * @receiver [Issue]
   * @return dia issue
   */
  private fun Issue.toDiaIssue(): DiaIssue =
      DiaIssue(id, severity.toDiaSeverity(), description, debt.toDiaDebt())

  /**
   * Convert current [Debt] to [DiaDebt].
   *
   * @receiver [Debt]
   * @return dia debt
   */
  private fun Debt.toDiaDebt() =
      DiaDebt(days, hours, mins)

  /**
   * Convert current [Severity] to [DiaSeverity].
   *
   * @receiver [Severity]
   * @return dia severity
   */
  private fun Severity.toDiaSeverity() =
      DiaSeverity.valueOf(toString())

  /**
   * Convert current [Entity] to [DiaEntity].
   *
   * @receiver [Entity]
   * @return instance of dia entity
   */
  private fun Entity.toDiaEntity(): DiaEntity =
      DiaEntity(name, className, signature, location.toDiaLocation())

  /**
   * Convert current [Metric] to [DiaProjectMetric].
   *
   * @receiver [Metric]
   * @return instance of dia metric
   */
  private fun Metric.toDiaMetric(): DiaProjectMetric =
      DiaProjectMetric(
          type,
          value,
          threshold,
          isDouble,
          conversionFactor
      )

  /**
   * Convert current [Location] to [DiaLocation].
   *
   * @receiver [Location]
   * @return instance of dia location
   */
  private fun Location.toDiaLocation(): DiaLocation =
      DiaLocation(
          source.toDiaSourceLocation(),
          text.toDiaTextLocation(),
          locationString,
          file
      )

  /**
   * Convert current [TextLocation] to [DiaTextLocation].
   *
   * @receiver [TextLocation]
   * @return instance of dia text location
   */
  private fun TextLocation.toDiaTextLocation(): DiaTextLocation =
      DiaTextLocation(start, end)

  /**
   * Convert current [SourceLocation] to [DiaSourceLocation].
   *
   * @receiver [SourceLocation]
   * @return instance of dia source location
   */
  private fun SourceLocation.toDiaSourceLocation(): DiaSourceLocation =
      DiaSourceLocation(line, column)

  /**
   * Convert current [ProjectMetric] to [DiaProjectMetric].
   *
   * @receiver [ProjectMetric]
   * @return instance of dia project metric
   */
  private fun ProjectMetric.toDiaMetric(): DiaProjectMetric =
      DiaProjectMetric(type, value, priority, isDouble, conversionFactor)


  /**
   * Convert current [Notification] to [DiaNotification].
   *
   * @receiver [Notification]
   * @return instance of dia notification
   */
  private fun Notification.toDiaNotification(): DiaNotification =
      DiaNotification(message)

}
