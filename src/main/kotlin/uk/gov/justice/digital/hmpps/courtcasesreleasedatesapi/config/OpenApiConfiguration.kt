package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration(buildProperties: BuildProperties) {
  private val version: String = buildProperties.version

  @Bean
  fun customOpenAPI(): OpenAPI = OpenAPI()
    .servers(
      listOf(
        Server().url("https://court-cases-release-dates-api-dev.hmpps.service.justice.gov.uk")
          .description("Development"),
        Server().url("https://court-cases-release-dates-api-preprod.hmpps.service.justice.gov.uk")
          .description("Pre-Production"),
        Server().url("https://court-cases-release-dates-api.hmpps.service.justice.gov.uk").description("Production"),
        Server().url("http://localhost:8080").description("Local"),
      ),
    )
    .tags(
      listOf(
        // TODO: Remove the Popular and Examples tag and start adding your own tags to group your resources
        Tag().name("Popular")
          .description("The most popular endpoints. Look here first when deciding which endpoint to use."),
        Tag().name("Examples").description("Endpoints for searching for a prisoner within a prison"),
      ),
    )
    .info(
      Info().title("HMPPS Court Cases Release Dates Api").version(version)
        .contact(Contact().name("HMPPS Digital Studio").email("feedback@digital.justice.gov.uk")),
    )
    .components(
      Components().addSecuritySchemes(
        "court-cases-release-dates-api-things-to-do-rw-role",
        SecurityScheme().addBearerJwtRequirement("COURT_CASES_RELEASE_DATES__PRE_SENTENCE_CALC_REVIEW_TASKS__RW"),
      ).addSecuritySchemes(
        "user-token",
        SecurityScheme().addBearerJwtRequirement("RELEASE_DATES_CALCULATOR")
          .description("The users token, with at least the RELEASE_DATES_CALCULATOR ro"),
      ),
    )
    .addSecurityItem(
      SecurityRequirement().addList(
        "court-cases-release-dates-api-things-to-do-rw-role",
        listOf("read", "write"),
      ).addList("user-token"),
    )
}

private fun SecurityScheme.addBearerJwtRequirement(role: String): SecurityScheme = type(SecurityScheme.Type.HTTP)
  .scheme("bearer")
  .bearerFormat("JWT")
  .`in`(SecurityScheme.In.HEADER)
  .name("Authorization")
  .description("A HMPPS Auth access token with the `$role` role.")
