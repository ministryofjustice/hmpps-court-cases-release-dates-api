package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = EmptyThingToDo::class)
@JsonSubTypes(
  JsonSubTypes.Type(value = EmptyThingToDo::class),
  JsonSubTypes.Type(value = AdjustmentThingToDo::class),
  JsonSubTypes.Type(value = CalculationToDoType::class),
)
interface ThingToDo {
  val count: Long
}
