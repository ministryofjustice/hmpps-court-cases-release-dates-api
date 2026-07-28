package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external

import java.time.LocalDate
import java.util.UUID

data class RemandAndSentencingThingsToDo(
  val prisonerId: String,
  val thingsToDo: List<RemandAndSentencingThingToDo>,
)

data class RemandAndSentencingThingToDo(
  val type: ThingToDoType,
  val hearingThingsToDoData: HearingThingsToDoData,
)

enum class ThingToDoType {
  NEW_REMAND_WARRANT,
  NEW_SENTENCING_WARRANT,
}

data class HearingThingsToDoData(
  val hearingId: UUID,
  val courtCaseReference: String,
  val hearingDate: LocalDate,
  val hearingType: String,
)
