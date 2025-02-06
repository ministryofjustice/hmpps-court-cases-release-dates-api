package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model

import com.fasterxml.jackson.annotation.JsonTypeInfo

// wrapping the thing to do in an object for caching null values.
@JsonTypeInfo(
  use = JsonTypeInfo.Id.CLASS,
  include = JsonTypeInfo.As.PROPERTY,
)
data class CacheableThingToDo(
  val thingToDo: ThingToDo? = null,
)
