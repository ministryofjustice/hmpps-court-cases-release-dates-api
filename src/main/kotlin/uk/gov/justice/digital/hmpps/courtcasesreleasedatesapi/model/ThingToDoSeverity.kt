package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

enum class ThingToDoSeverity(val severityRank: Int) {
  NOTIFICATION(1),
  REQUIRED_BEFORE_CALCULATION(2),
}
