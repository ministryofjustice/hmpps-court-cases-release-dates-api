package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

// wrapping the thing to do in an object for caching null values.
data class CacheableThingToDo(
  val thingToDo: ThingToDo? = null,
)
