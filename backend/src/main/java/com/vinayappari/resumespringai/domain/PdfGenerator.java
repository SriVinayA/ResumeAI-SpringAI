package com.vinayappari.resumespringai.domain;

import com.vinayappari.resumespringai.dto.ResumeData;
import java.io.File;

public interface PdfGenerator {
    File generatePdf(ResumeData resume);
}
