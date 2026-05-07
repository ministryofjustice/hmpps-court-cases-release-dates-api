package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ThingsToDoTest {

  @Test
  fun `should keep only highest severity things`() {
    val notificationThing = thingToDo(
      type = ThingToDoType.HMCTS_API_DOCUMENT_RECEIVED,
    )

    val requiredThing1 = thingToDo(
      type = ThingToDoType.CALCULATION_REQUIRED,
    )

    val requiredThing2 = thingToDo(
      type = ThingToDoType.ADA_INTERCEPT,
    )

    val result = ThingsToDo(
      listOf(notificationThing, requiredThing1, requiredThing2),
    )

    assertThat(result.count).isEqualTo(2)

    assertThat(result.severity)
      .isEqualTo(ThingToDoSeverity.REQUIRED_BEFORE_CALCULATION)

    assertThat(result.things)
      .containsExactly(requiredThing1, requiredThing2)
  }

  @Test
  fun `should return all things when all have same severity`() {
    val thing1 = thingToDo(
      type = ThingToDoType.CALCULATION_REQUIRED,
    )

    val thing2 = thingToDo(
      type = ThingToDoType.ADA_INTERCEPT,
    )

    val result = ThingsToDo(listOf(thing1, thing2))

    assertThat(result.count).isEqualTo(2)

    assertThat(result.severity)
      .isEqualTo(ThingToDoSeverity.REQUIRED_BEFORE_CALCULATION)

    assertThat(result.things)
      .containsExactly(thing1, thing2)
  }

  @Test
  fun `should return empty values when list is empty`() {
    val result = ThingsToDo(emptyList())

    assertThat(result.count).isEqualTo(0)
    assertThat(result.severity).isNull()
    assertThat(result.things).isEmpty()
  }

  private fun thingToDo(type: ThingToDoType): ThingToDo = ThingToDo(
    "Title",
    "Message",
    "Button",
    "www.google.com",
    type = type,
  )
}
