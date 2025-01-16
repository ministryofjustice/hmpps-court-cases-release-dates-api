package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2

data class ThingsToDo(
  val things: List<ThingToDo>,
  val count: Int = things.size,
)
