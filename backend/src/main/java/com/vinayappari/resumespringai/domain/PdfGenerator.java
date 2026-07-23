package com.vinayappari.resumespringai.domain;

import com.vinayappari.resumespringai.dto.ResumeData;
import java.io.File;

public interface PdfGenerator {
    public record PdfResult(File pdf, String overleafZipBase64) {}
    PdfResult generatePdf(ResumeData resume);
}
