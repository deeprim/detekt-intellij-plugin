package org.intellij.detekt.api

/**
 * Represents detekt's notificatino.
 */
data class DiaNotification(val message: String)

/**
 * Represents `detekt`'s project metric.
 */
data class DiaProjectMetric(
    val type: String,
    val value: Int,
    val priority: Int = -1,
    val isDouble: Boolean = false,
    val conversionFactor: Int = 100
)

typealias RuleSetId = String

/**
 * Represents `detekt`'s finding.
 */
class DiaFinding(
    val id: String,
    val issue: DiaIssue,
    val references: List<DiaEntity>,
    val message: String,
    val messageOrDescription: String,

    override val entity: DiaEntity
) : HasEntity

/**
 * Describes source code position.
 */
interface HasEntity {
  val entity: DiaEntity
  val location: DiaLocation
    get() = entity.location
  val locationAsString: String
    get() = location.locationString
  val startPosition: DiaSourceLocation
    get() = location.source
  val charPosition: DiaTextLocation
    get() = location.text

  val file: String
    get() = location.file

  // signature?

  val name: String
    get() = entity.name
  val inClass: String
    get() = entity.className
}

data class DiaEntity(
    val name: String,
    val className: String,
    val singnature: String,
    val location: DiaLocation

    // val ktElement: KtElement ommited
)

data class DiaLocation(
    val source: DiaSourceLocation,
    val text: DiaTextLocation,
    val locationString: String,
    val file: String
)

/**
 * Stores line and column information of a location.
 */
data class DiaSourceLocation(val line: Int, val column: Int) {
  override fun toString(): String = "$line:$column"
}

/**
 * Stores character start and end positions of an text file.
 */
data class DiaTextLocation(val start: Int, val end: Int) {
  override fun toString(): String = "$start:$end"
}

data class DiaIssue(
    val id: String,
    val severity: DiaSeverity,
    val description: String,
    val debt: DiaDebt
)

/**
 * Rules can classified into different severity grades. Maintainer can choose
 * a grade which is most harmful to their projects.
 */
enum class DiaSeverity {
  CodeSmell, Style, Warning, Defect, Minor, Maintainability, Security, Performance
}

/**
 * Debt describes the estimated amount of work needed to fix a given issue.
 */
@Suppress("MagicNumber")
data class DiaDebt(val days: Int = 0, val hours: Int = 0, val mins: Int = 0) {

  init {
    require(days >= 0 && hours >= 0 && mins >= 0)
    require(!(days == 0 && hours == 0 && mins == 0))
  }

  companion object {
    val TWENTY_MINS = DiaDebt(0, 0, 20)
    val TEN_MINS = DiaDebt(0, 0, 10)
    val FIVE_MINS = DiaDebt(0, 0, 5)
  }

  override fun toString(): String = buildString {
    if (days > 0) append("${days}d ")
    if (hours > 0) append("${hours}h ")
    if (mins > 0) append("${mins}min")
  }.trim()

}
