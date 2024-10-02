package com.distribuidos.transfers.services.facades.authentication;

import com.distribuidos.transfers.config.EnvironmentConfig;
import com.distribuidos.transfers.exceptions.AuthenticationException;
import com.distribuidos.transfers.models.ResponseBody;
import com.distribuidos.transfers.services.facades.authentication.models.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import static com.distribuidos.transfers.exceptions.ErrorCodes.AUTHENTICATION_UPSTREAM_ERROR;
import static reactor.core.publisher.Mono.error;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final WebClient webClient;
    private final EnvironmentConfig environmentConfig;

    private static final ParameterizedTypeReference<ResponseBody<Boolean>> RESPONSE_TYPE_REGISTER =
            new ParameterizedTypeReference<>() {
            };

    public Mono<Boolean> registerExternalUser(String citizenName, String citizenEmail) {
        String resourceUri = environmentConfig.getDomains().getAuthenticationDomain() + "/auth/register";
        UserRequest userRequest = UserRequest.builder()
                .status("External")
                .fullName(citizenName)
                .email(citizenEmail)
                .password("1234567890")
                .build();

        return webClient
                .post()
                .uri(resourceUri)
                .bodyValue(userRequest)
                .exchangeToMono(authResponse -> {
                    HttpStatus httpStatus = HttpStatus.valueOf(authResponse.statusCode().value());
                    if (HttpStatus.OK.equals(httpStatus) || HttpStatus.CREATED.equals(httpStatus)) {
                        return authResponse.bodyToMono(RESPONSE_TYPE_REGISTER)
                                .map(ResponseBody::getResult);
                    }
                    HttpHeaders responseHeaders = authResponse.headers().asHttpHeaders();
                    return authResponse.bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                log.error("{} - The authentication service responded with "
                                                + "an unexpected failure response for: {}"
                                                + "\nStatus Code: {}\nResponse Headers: {}\nResponse Body: {}",
                                        AUTHENTICATION_UPSTREAM_ERROR, resourceUri, httpStatus, responseHeaders,
                                        responseBody);
                                return error(new AuthenticationException(responseBody));
                            });
                })
                .retryWhen(Retry
                        .max(environmentConfig.getServiceRetry().getMaxAttempts())
                        .filter(AuthenticationException.class::isInstance)
                        .onRetryExhaustedThrow((ignore1, ignore2) -> ignore2.failure()));

    }
}
