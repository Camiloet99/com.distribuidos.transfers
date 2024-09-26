package com.distribuidos.transfers.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomMultipartFile implements MultipartFile {

    private String name;
    private String originalFilename;
    private String contentType;
    private byte[] content;

    @Override
    public boolean isEmpty() {
        return this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @NotNull
    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @NotNull
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(@NotNull File dest) throws IOException, IllegalStateException {
        try (OutputStream outStream = new FileOutputStream(dest)) {
            outStream.write(this.content);
        }
    }
}
