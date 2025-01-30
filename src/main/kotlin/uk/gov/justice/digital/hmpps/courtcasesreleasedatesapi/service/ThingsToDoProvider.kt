package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.cache.annotation.Cacheable
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CacheConstants
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

interface ThingsToDoProvider {
  val serviceName: String

  @Cacheable(CacheConstants.THINGS_TO_DO, key = "#root.target.thingToDoType().name().concat('-').concat(#prisonerId)")
  fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): ThingToDo?

  fun additionalRoles(): List<String> = emptyList()

  fun thingToDoType(): ThingToDoType
}
