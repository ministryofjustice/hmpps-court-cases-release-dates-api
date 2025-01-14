package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType

data class CalculationThingToDo(
  override val count: Long,
  val type: CalculationToDoType,
) : ThingToDo
