package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType

data class AdjustmentThingsToDo(
  val prisonerId: String,
  val thingsToDo: List<AdjustmentToDoType> = emptyList(),
)
