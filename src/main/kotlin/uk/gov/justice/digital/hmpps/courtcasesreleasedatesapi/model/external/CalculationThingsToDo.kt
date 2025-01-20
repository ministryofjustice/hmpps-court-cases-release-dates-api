package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType

data class CalculationThingsToDo(
  val prisonerId: String,
  val thingsToDo: List<CalculationToDoType> = emptyList(),
)
