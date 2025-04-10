import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WikipediaSummaryResponse(
    val extract: String?,
    val description: String?,
    val thumbnail: Thumbnail?,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Thumbnail(
        val source: String,
    )
}
