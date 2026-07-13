package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external

import java.util.UUID

data class RemandAndSentencingThingsToDo(
  val prisonerId: String,
  val thingsToDo: List<ThingToDoType> = emptyList(),
  val hearingThingsToDoData: HearingThingsToDoData?,
)

enum class ThingToDoType {
  NEW_REMAND_WARRANT,
  NEW_SENTENCING_WARRANT,
}

data class HearingThingsToDoData(
  val hearingId: UUID,
  val courtCaseReference: String,
)
