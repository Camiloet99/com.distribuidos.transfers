package com.distribuidos.transfers.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferObject {

    Integer id;
    String citizenName;
    String citizenEmail;
    Documents documents;
    String confirmationURL;

    @Value
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Documents {

        Map<String, List<String>> documentMap;

    }
}
