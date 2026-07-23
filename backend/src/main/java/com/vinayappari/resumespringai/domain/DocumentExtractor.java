package com.vinayappari.resumespringai.domain;

import com.vinayappari.resumespringai.dto.ExtractionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentExtractor {
    ExtractionResponse extract(MultipartFile file);
}
