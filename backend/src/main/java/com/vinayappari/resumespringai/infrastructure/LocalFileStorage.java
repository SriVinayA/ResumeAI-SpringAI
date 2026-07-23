package com.vinayappari.resumespringai.infrastructure;

import com.vinayappari.resumespringai.domain.FileStorage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalFileStorage implements FileStorage {

    private final Map<String, File> pdfStore = new ConcurrentHashMap<>();

    @Override
    public String storeFile(File file) {
        String fileId = java.util.UUID.randomUUID().toString();
        pdfStore.put(fileId, file);
        return fileId;
    }

    @Override
    public Optional<File> getFile(String fileId) {
        return Optional.ofNullable(pdfStore.get(fileId));
    }
}
