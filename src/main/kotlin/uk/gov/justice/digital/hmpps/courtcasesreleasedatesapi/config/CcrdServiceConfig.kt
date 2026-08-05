package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

data class CcrdServiceConfig(
  val uiUrl: String,
  val urlMapping: String,
  val requiredRoles: List<String>, // requireAnyRoles so works on OR not just AND
  val text: String,
  val maintenanceAlert: MaintenanceAlertConfig,
) {
  init {
    if (!urlMapping.contains("{prisonerId}")) {
      throw IllegalStateException("Service configuration url mappings must contain {prisonerId}")
    }
  }
}
