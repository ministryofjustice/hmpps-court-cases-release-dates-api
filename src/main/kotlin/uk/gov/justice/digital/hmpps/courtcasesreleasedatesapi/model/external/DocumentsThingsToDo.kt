package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external

data class DocumentsThingsToDo(
  val prisonerId: String,
  val thingsToDo: List<DocumentsToDoType> = emptyList(),
)
enum class DocumentsToDoType {
  HMCTS_API_DOCUMENT_RECEIVED,
}
