package org.intellij.detekt.api

/**
 * Accessor to [Detektion].
 */
class DiaDetektion(
    val findings: Map<RuleSetId, List<DiaFinding>>,

    val notifications: Collection<DiaNotification>,
    val metrics: Collection<DiaProjectMetric>
)
