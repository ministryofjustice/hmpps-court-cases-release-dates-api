package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.cache.annotation.Cacheable
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CacheConstants
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDoProviderName

interface CacheableThingsToDoProvider : ThingsToDoProvider {
  @Cacheable(CacheConstants.THINGS_TO_DO, key = "#root.target.thingsToDoProviderName().name().concat('-').concat(#prisonerId)")
  override fun getThingsToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): List<ThingToDo>

  fun thingsToDoProviderName(): ThingsToDoProviderName
}
