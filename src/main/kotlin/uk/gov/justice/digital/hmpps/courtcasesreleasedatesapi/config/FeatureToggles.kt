package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "feature-toggles")
data class FeatureToggles(
  var thingsToDo: Boolean = false,
)
