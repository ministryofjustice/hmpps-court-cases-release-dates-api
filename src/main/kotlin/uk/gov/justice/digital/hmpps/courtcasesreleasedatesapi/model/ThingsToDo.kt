package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType

data class ThingsToDo(
  val prisonerId: String,
  val calculationThingsToDo: List<CalculationToDoType> = emptyList(),
  val adjustmetThingsToDo: List<AdjustmentToDoType> = emptyList(),
)
