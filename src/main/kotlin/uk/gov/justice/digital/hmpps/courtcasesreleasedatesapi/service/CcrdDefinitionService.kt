package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfigs
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.FeatureToggles
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.CcrdServiceDefinition
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.CcrdServiceDefinitions
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.hmpps.kotlin.auth.HmppsAuthenticationHolder

@Service
class CcrdDefinitionService(
  private val featureToggles: FeatureToggles,
  private val ccrdServiceConfigs: CcrdServiceConfigs,
  private val thingsToDoProviders: List<ThingsToDoProvider>,
) {

  fun getCcrdConfiguration(prisonerId: String): CcrdServiceDefinitions {
    val thingsToDo: MutableList<ThingsToDo> = mutableListOf()
    return CcrdServiceDefinitions(
      ccrdServiceConfigs.services
        .filter { (_, serviceConfig) ->
          roleCheck(serviceConfig)
        }
        .mapValues { (serviceName, serviceConfig) ->
          val thingToDo = getThingsToDo(prisonerId, serviceName, thingsToDo)
          thingsToDo.add(thingToDo)
          CcrdServiceDefinition(
            href = getHref(serviceConfig, prisonerId),
            text = serviceConfig.text,
            thingsToDo = thingToDo,
          )
        },
    )
  }

  private fun getHref(serviceConfig: CcrdServiceConfig, prisonerId: String): String {
    return serviceConfig.uiUrl + serviceConfig.urlMapping.replace("{prisonerId}", prisonerId)
  }

  private fun getThingsToDo(prisonerId: String, serviceName: String, thingsToDo: MutableList<ThingsToDo>): ThingsToDo {
    if (featureToggles.thingsToDo) {
      // TODO build caching layer around things to do.
      val provider = thingsToDoProviders.find { it.serviceName == serviceName }
      if (provider != null) {
        return provider.getThingToDo(prisonerId, thingsToDo, ccrdServiceConfigs.services[serviceName]!!)
      }
    }
    return ThingsToDo(emptyList())
  }

  private fun roleCheck(serviceConfig: CcrdServiceConfig): Boolean =
    HmppsAuthenticationHolder.hasRoles(*serviceConfig.requiredRoles.toTypedArray())
}
