package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

interface ThingsToDoProvider {
  val serviceName: String

  fun getThingsToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): List<ThingToDo>

  fun additionalRoles(): List<String> = emptyList()
}
