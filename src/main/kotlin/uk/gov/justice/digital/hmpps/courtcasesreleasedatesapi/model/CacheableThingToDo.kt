package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import com.fasterxml.jackson.annotation.JsonTypeInfo

// wrapping the thing to do in an object for caching null values.
@JsonTypeInfo(
  use = JsonTypeInfo.Id.MINIMAL_CLASS,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@class",
)
data class CacheableThingToDo(
  val thingToDo: ThingToDo? = null,
)
