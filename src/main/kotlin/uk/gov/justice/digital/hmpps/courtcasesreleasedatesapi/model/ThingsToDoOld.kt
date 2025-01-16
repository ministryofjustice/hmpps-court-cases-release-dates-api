package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType

@Deprecated("Use new service definitions endpoint.")
data class ThingsToDoOld(
  val prisonerId: String,
  val calculationThingsToDo: List<CalculationToDoType> = emptyList(),
  val adjustmentThingsToDo: AdjustmentThingsToDo? = null,
  val hasAdjustmentThingsToDo: Boolean = false,
  val hasCalculationThingsToDo: Boolean = false,
)
