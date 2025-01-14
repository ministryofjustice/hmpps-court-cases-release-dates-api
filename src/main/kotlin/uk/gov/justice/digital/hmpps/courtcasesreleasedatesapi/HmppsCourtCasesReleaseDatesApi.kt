package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class HmppsCourtCasesReleaseDatesApi

fun main(args: Array<String>) {
  runApplication<HmppsCourtCasesReleaseDatesApi>(*args)
}
