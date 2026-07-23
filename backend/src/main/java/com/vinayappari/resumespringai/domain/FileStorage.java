package com.vinayappari.resumespringai.domain;

import java.io.File;
import java.util.Optional;

public interface FileStorage {
    String storeFile(File file);
    Optional<File> getFile(String fileId);
}
