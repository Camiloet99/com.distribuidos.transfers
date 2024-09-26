package com.distribuidos.transfers.services.facades.authentication.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    
    Long documentId;
    String fullName;
    String status;
    String email;
    String description;
    String password;
    String address;
    String token;
    
}
