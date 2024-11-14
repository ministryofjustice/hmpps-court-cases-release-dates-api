package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType

data class AdjustmentThingsToDo(
  val prisonerId: String,
  val thingsToDo: List<AdjustmentToDoType> = emptyList(),
  val adaIntercept: AdaIntercept? = null,
)

data class AdaIntercept(
  val type: InterceptType,
  val number: Int,
  val anyProspective: Boolean,
  val messageArguments: List<String> = listOf(),
)

enum class InterceptType {
  NONE,
  FIRST_TIME,
  UPDATE,
  PADA,
}
