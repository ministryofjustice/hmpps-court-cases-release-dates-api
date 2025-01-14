package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("ccrd-service-configurations")
data class CcrdServiceConfigs(val services: Map<String, CcrdServiceConfig>)
