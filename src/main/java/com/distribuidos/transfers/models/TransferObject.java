package com.distribuidos.transfers.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferObject {

    private Integer id;
    private String citizenName;
    private String citizenEmail;
    private List<Map<String, List<String>>> documents;
}
