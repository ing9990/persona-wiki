package io.ing9990.authentication.client.config

import io.netty.channel.ChannelOption
import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider

@Configuration
class WebClientConfig {
    var factory: DefaultUriBuilderFactory = DefaultUriBuilderFactory()

    var httpClient: HttpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)

    @Bean
    fun webClient(): WebClient {
        factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY

        return WebClient.builder()
            .uriBuilderFactory(factory)
            .codecs { configurer: ClientCodecConfigurer ->
                configurer.defaultCodecs()
                    .maxInMemorySize(2 * 1024 * 1024)
            }
            .clientConnector(
                ReactorClientHttpConnector(
                    httpClient
                )
            )
            .build()
    }

    @Bean
    fun connectionProvider(): ConnectionProvider {
        return ConnectionProvider.builder("http-pool")
            .maxConnections(100)
            .pendingAcquireTimeout(Duration.ofMillis(0))
            .pendingAcquireMaxCount(-1)
            .maxIdleTime(Duration.ofMillis(1000L))
            .build()
    }
}
