package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.ThingsToDo

interface ThingsToDoProvider {
  val serviceName: String
  fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): ThingsToDo
}
