package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

/*
  A list of things to do for a given service.
  If a service has multiple things to do of different severities, only show the most severe things to do.
 */
class ThingsToDo(
  things: List<ThingToDo>,
) {
  val things: List<ThingToDo>
  val count: Int
  val severity: ThingToDoSeverity?

  init {
    this.severity = things.maxByOrNull { it.type.severity.severityRank }?.type?.severity
    this.things = things.filter { it.type.severity == severity }
    this.count = this.things.size
  }
}
