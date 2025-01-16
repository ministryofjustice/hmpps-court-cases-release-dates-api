package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

data class CcrdServiceConfig(
  val uiUrl: String,
  val urlMapping: String,
  val requiredRoles: List<String>,
  val text: String,
) {
  init {
    if (!urlMapping.contains("{prisonerId}")) {
      throw IllegalStateException("Service configuration url mappings must contain {prisonerId}")
    }
  }
}
