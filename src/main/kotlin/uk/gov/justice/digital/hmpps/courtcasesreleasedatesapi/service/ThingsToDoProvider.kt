package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.ThingToDo

interface ThingsToDoProvider {
  val serviceName: String
  fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingToDo>): ThingToDo
}
