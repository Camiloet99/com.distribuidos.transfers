package com.distribuidos.transfers.services.facades.documents;

import com.distribuidos.transfers.config.EnvironmentConfig;
import com.distribuidos.transfers.exceptions.DocumentsPushError;
import com.distribuidos.transfers.models.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.List;

import static com.distribuidos.transfers.exceptions.ErrorCodes.DOCUMENTS_UPSTREAM_ERROR;
import static reactor.core.publisher.Mono.error;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentsFacade {

    private final EnvironmentConfig environmentConfig;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<ResponseBody<List<String>>> RESPONSE_TYPE_DOCUMENTS =
            new ParameterizedTypeReference<>() {
            };

    public Mono<List<String>> pushExternalUserDocuments(List<MultipartFile> files, String userId) {
        String resourceUri = environmentConfig.getDomains().getDocumentsDomain()
                + String.format("/upload/all/%s", userId);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        for (MultipartFile file : files) {
            formData.add("files", file.getResource());
        }

        return webClient
                .post()
                .uri(resourceUri)
                .body(BodyInserters.fromMultipartData(formData))
                .exchangeToMono(documentsResponse -> {
                    HttpStatus httpStatus = HttpStatus.valueOf(documentsResponse.statusCode().value());
                    if (HttpStatus.OK.equals(httpStatus)) {
                        return documentsResponse.bodyToMono(RESPONSE_TYPE_DOCUMENTS)
                                .map(ResponseBody::getResult);
                    }

                    HttpHeaders responseHeaders = documentsResponse.headers().asHttpHeaders();
                    return documentsResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                log.error("{} - The documents service responded with "
                                                + "an unexpected failure response for: {}"
                                                + "\nStatus Code: {}\nResponse Headers: {}\nResponse Body: {}",
                                        DOCUMENTS_UPSTREAM_ERROR, resourceUri, httpStatus, responseHeaders,
                                        responseBody);
                                return error(new DocumentsPushError(responseBody));
                            });
                })
                .retryWhen(Retry
                        .max(environmentConfig.getServiceRetry().getMaxAttempts())
                        .filter(DocumentsPushError.class::isInstance)
                        .onRetryExhaustedThrow((ignore1, ignore2) -> ignore2.failure()));

    }
}
