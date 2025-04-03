package io.ing9990.authentication.util

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class WebClientUtil(private val webClient: WebClient) {
    fun <T> get(
        url: String,
        responseType: Class<T>,
    ): Mono<T> {
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(responseType)
    }

    fun <T> get(
        url: String,
        headers: MultiValueMap<String?, String?>,
        responseType: Class<T>,
    ): Mono<T> {
        return webClient.get()
            .uri(url)
            .headers { httpHeaders: HttpHeaders -> httpHeaders.addAll(headers) }
            .retrieve()
            .bodyToMono(responseType)
    }

    fun <T> getWithParams(
        url: String,
        params: MultiValueMap<String?, String?>?,
        responseType: Class<T>,
    ): Mono<T> {
        return webClient.get()
            .uri(
                UriComponentsBuilder.fromHttpUrl(url)
                    .queryParams(params).build().toUri(),
            )
            .retrieve()
            .bodyToMono(responseType)
    }

    fun <T> post(
        url: String,
        requestBody: Any,
        mediaType: MediaType,
        responseType: Class<T>,
    ): Mono<T> {
        return webClient.post()
            .uri(url)
            .contentType(mediaType)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(responseType)
    }

    fun <T> post(
        url: String,
        requestBody: Any,
        headers: MultiValueMap<String?, String?>,
        responseType: Class<T>,
    ): Mono<T> {
        return webClient.post()
            .uri(url)
            .headers { httpHeaders: HttpHeaders -> httpHeaders.addAll(headers) }
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(responseType)
    }

    fun <T> postFormData(
        url: String,
        formData: MultiValueMap<String?, String?>,
        responseType: Class<T>,
    ): Mono<T> {
        return webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(responseType)
    }

    fun <T> postFormData(
        url: String,
        formData: MultiValueMap<String?, String?>,
        headers: MultiValueMap<String?, String?>,
        responseType: Class<T>,
    ): Mono<T> {
        return webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers { httpHeaders: HttpHeaders -> httpHeaders.addAll(headers) }
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(responseType)
    }
}
