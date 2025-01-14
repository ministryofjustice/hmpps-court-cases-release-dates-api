package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.AdaIntercept

data class AdjustmentThingToDo(
  override val count: Long,
  val types: List<AdjustmentToDoType> = emptyList(),
  val adaIntercept: AdaIntercept? = null,
) : ThingToDo
