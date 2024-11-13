package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.ToDoType

data class ThingsToDo(
  val prisonerId: String,
  val thingsToDo: List<ToDoType> = emptyList(),
)
