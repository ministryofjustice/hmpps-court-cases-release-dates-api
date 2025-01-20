package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

data class ThingsToDo(
  val things: List<ThingToDo>,
  val count: Int = things.size,
)
