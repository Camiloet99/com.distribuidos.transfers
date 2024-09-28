package com.distribuidos.transfers.services;

import com.distribuidos.transfers.models.CustomMultipartFile;
import com.distribuidos.transfers.models.TransferObject;
import com.distribuidos.transfers.services.facades.authentication.AuthenticationFacade;
import com.distribuidos.transfers.services.facades.documents.DocumentsFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransfersService {

    private final AuthenticationFacade authenticationFacade;
    private final DocumentsFacade documentsFacade;
    private final WebClient webClient;

    public void processTransfer(Map<String, Object> transferData) {
        TransferObject transferObject = mapToTransferObject(transferData);

        authenticationFacade.registerExternalUser(transferObject.getCitizenName(), transferObject.getCitizenEmail())
                .then(downloadDocuments(transferObject.getDocuments()).collectList())
                .flatMap(files -> documentsFacade
                        .pushExternalUserDocuments(files, String.valueOf(transferObject.getId())))
                .doOnSuccess(uploadedLinks ->
                        log.info("Success! User ID: " + transferObject.getId() + " - Document links: " + uploadedLinks))
                .doOnError(throwable ->
                        log.error("Error pushing user " + transferObject.getId() + " data, cause: "
                                + throwable.getMessage(), throwable))
                .subscribe();
    }

    private Flux<MultipartFile> downloadDocuments(List<Map<String, List<String>>> documents) {
        List<Mono<MultipartFile>> fileMonos = new ArrayList<>();
        for (Map<String, List<String>> documentMap : documents) {
            for (Map.Entry<String, List<String>> entry : documentMap.entrySet()) {
                String documentName = entry.getKey();
                List<String> links = entry.getValue();

                if (!links.isEmpty()) {
                    String downloadLink = links.get(0);
                    // Crear un Mono que descargue el archivo y lo convierta en MultipartFile
                    Mono<MultipartFile> fileMono = webClient
                            .get()
                            .uri(downloadLink)
                            .retrieve()
                            .bodyToMono(byte[].class) // Obtener el cuerpo de la respuesta como un array de bytes
                            .map(fileContent -> new CustomMultipartFile(documentName, documentName,
                                    "application/octet-stream", fileContent));

                    fileMonos.add(fileMono);
                }
            }
        }
        return Flux.concat(fileMonos);
    }

    private TransferObject mapToTransferObject(Map<String, Object> transferData) {
        return TransferObject.builder()
                .citizenEmail((String) transferData.get("citizenEmail"))
                .id((Integer) transferData.get(("id")))
                .citizenName((String) transferData.get("citizenName"))
                .citizenName((String) transferData.get("citizenName"))
                .documents((List<Map<String, List<String>>>) transferData.get("documents"))
                .build();
    }
}
