package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
          roleCheck(serviceConfig.requiredRoles)
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
      val providers = thingsToDoProviders
        .filter { it.serviceName == serviceName }
        .filter { it.additionalRoles().isEmpty() || roleCheck(it.additionalRoles()) }
      return ThingsToDo(
        providers.mapNotNull {
          try {
            it.getThingToDo(prisonerId, thingsToDo, ccrdServiceConfigs.services[serviceName]!!).thingToDo
          } catch (error: Exception) {
            log.error("Error finding thing to do $prisonerId $serviceName", error)
            null
          }
        },
      )
    }
    return ThingsToDo(emptyList())
  }

  private fun roleCheck(requiredRoles: List<String>): Boolean =
    HmppsAuthenticationHolder.hasRoles(*requiredRoles.toTypedArray())

  companion object {
    private val log: Logger = LoggerFactory.getLogger(CcrdDefinitionService::class.java)
  }
}
