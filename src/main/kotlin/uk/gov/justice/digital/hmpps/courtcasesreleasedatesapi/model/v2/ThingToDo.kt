package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2

data class ThingToDo(
  val title: String,
  val message: String,
  val buttonText: String,
  val buttonHref: String,
  val type: ThingToDoType,
)
