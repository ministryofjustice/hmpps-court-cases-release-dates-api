package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

data class ThingToDo(
  val title: String,
  val message: String,
  val buttonText: String,
  val buttonHref: String,
  val type: ThingToDoType,
  val messageIsHtml: Boolean = false,
) {
  companion object {
    fun notificationOnlyThingToDo(type: ThingToDoType): ThingToDo = ThingToDo(
      "",
      "",
      "",
      "",
      type,
    )
  }
}
