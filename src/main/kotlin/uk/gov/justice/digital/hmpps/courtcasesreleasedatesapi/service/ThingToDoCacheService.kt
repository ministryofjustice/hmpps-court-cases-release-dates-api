package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CacheConstants
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDoProviderName

@Service
class ThingToDoCacheService {

  @CacheEvict(CacheConstants.THINGS_TO_DO, key = "#thingsToDoProviderName.name().concat('-').concat(#prisonerId)")
  fun evictCache(thingsToDoProviderName: ThingsToDoProviderName, prisonerId: String) {}
}
