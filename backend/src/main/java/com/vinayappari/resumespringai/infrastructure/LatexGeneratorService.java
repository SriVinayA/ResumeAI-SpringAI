package com.vinayappari.resumespringai.infrastructure;

import com.vinayappari.resumespringai.domain.PdfGenerator;
import com.vinayappari.resumespringai.dto.ResumeData;
import com.vinayappari.resumespringai.util.LatexEscapeUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LatexGeneratorService implements PdfGenerator {

    @Override
    public PdfResult generatePdf(ResumeData resume) {
        try {
            // 1. Read the template
            File templateFile = new ClassPathResource("template.tex").getFile();
            String templateContent = Files.readString(templateFile.toPath());

            // 2. Replace placeholders with formatted LaTeX
            String texContent = injectData(templateContent, resume);

            // 3. Set up the directory
            Path tempDir = Path.of("src", "main", "resources", "generated-resumes");
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }

            // 4. COPY THE resume.cls FILE to the generated folder
            Path clsDest = tempDir.resolve("resume.cls");
            if (!Files.exists(clsDest)) {
                try (InputStream clsStream = new ClassPathResource("resume.cls").getInputStream()) {
                    Files.copy(clsStream, clsDest, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // 5. Write generated .tex
            String fileBaseName = "resume_" + UUID.randomUUID();
            Path texFilePath = tempDir.resolve(fileBaseName + ".tex");
            Files.writeString(texFilePath, texContent);

            // Create Base64 zip for Overleaf
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
                zos.putNextEntry(new java.util.zip.ZipEntry("resume.cls"));
                Files.copy(clsDest, zos);
                zos.closeEntry();
                zos.putNextEntry(new java.util.zip.ZipEntry("main.tex"));
                zos.write(texContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            String overleafZipBase64 = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());

            // 6. Compile using pdflatex inside a Docker container
            Process process = getProcess(tempDir, fileBaseName);

            // Optional: Print Docker output to your IDE console for debugging
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Docker pdflatex] " + line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Docker LaTeX compilation failed with exit code " + exitCode);
            }

            return new PdfResult(tempDir.resolve(fileBaseName + ".pdf").toFile(), overleafZipBase64);

        } catch (Exception e) {
            throw new RuntimeException("Error generating LaTeX PDF: " + e.getMessage(), e);
        }
    }

    private static @NonNull Process getProcess(Path tempDir, String fileBaseName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker",
                "run",
                "--rm", // Automatically remove the container when it exits
                "-v", tempDir.toAbsolutePath() + ":/workdir", // Mount our generated folder into the container
                "-w", "/workdir", // Set the working directory inside the container
                "texlive/texlive:latest", // The official TeX Live Docker image
                "pdflatex",
                "-interaction=nonstopmode",
                fileBaseName + ".tex" // Just the filename, since we are inside /workdir
        );

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        return process;
    }

    private String injectData(String template, ResumeData resume) {
        String result = template;

        // Basic Strings
        result = result.replace("{{NAME}}", resume.name() != null ? LatexEscapeUtil.escape(resume.name()) : "");
        result = result.replace("{{SUMMARY}}", resume.summary() != null ? LatexEscapeUtil.escape(resume.summary()) : "");

        java.util.List<String> contactInfo = new java.util.ArrayList<>();
        if (resume.email() != null && !resume.email().isBlank()) {
            contactInfo.add("\\href{mailto:" + LatexEscapeUtil.escape(resume.email()) + "}{" + LatexEscapeUtil.escape(resume.email()) + "}");
        }
        if (resume.phone() != null && !resume.phone().isBlank()) {
            contactInfo.add("\\href{tel:" + LatexEscapeUtil.escape(resume.phone()) + "}{" + LatexEscapeUtil.escape(resume.phone()) + "}");
        }
        if (resume.location() != null && !resume.location().isBlank()) {
            contactInfo.add(LatexEscapeUtil.escape(resume.location()));
        }
        if (resume.linkedin() != null && !resume.linkedin().isBlank()) {
            contactInfo.add("\\href{" + LatexEscapeUtil.escape(resume.linkedin()) + "}{LinkedIn}");
        }
        if (resume.github() != null && !resume.github().isBlank()) {
            contactInfo.add("\\href{" + LatexEscapeUtil.escape(resume.github()) + "}{GitHub}");
        }
        if (resume.portfolio() != null && !resume.portfolio().isBlank()) {
            contactInfo.add("\\href{" + LatexEscapeUtil.escape(resume.portfolio()) + "}{Portfolio}");
        }
        if (resume.otherLinks() != null) {
            for (String link : resume.otherLinks()) {
                if (link != null && !link.isBlank()) {
                    contactInfo.add("\\href{" + LatexEscapeUtil.escape(link) + "}{" + LatexEscapeUtil.escape(link) + "}");
                }
            }
        }
        result = result.replace("{{CONTACT_INFO}}", String.join(" $|$ ", contactInfo));

        // Skills (Categorized list)
        StringBuilder skillsBuilder = new StringBuilder();
        if (resume.skills() != null) {
            for (int i = 0; i < resume.skills().size(); i++) {
                ResumeData.SkillCategory sc = resume.skills().get(i);
                String category = LatexEscapeUtil.escape(sc.category());
                String items = String.join(", ", sc.skills().stream().map(LatexEscapeUtil::escape).toList());
                skillsBuilder.append("\\textbf{").append(category).append("}{: ").append(items).append("}");
                if (i < resume.skills().size() - 1) {
                    skillsBuilder.append(" \\\\\n    ");
                }
            }
        }
        result = result.replace("{{SKILLS}}", skillsBuilder.toString());

        // Experience Lists
        StringBuilder expBuilder = new StringBuilder();
        if (resume.experience() != null && !resume.experience().isEmpty()) {
            expBuilder.append("%-----------EXPERIENCE-----------\n");
            expBuilder.append("\\section{Work Experience}\n");
            expBuilder.append("\\resumeSubHeadingListStart\n");
            for (ResumeData.Experience exp : resume.experience()) {
                expBuilder.append("\\resumeSubheading\n")
                        .append("  {").append(LatexEscapeUtil.escape(exp.title())).append("}{").append(LatexEscapeUtil.escape(exp.duration())).append("}\n")
                        .append("  {").append(LatexEscapeUtil.escape(exp.company())).append("}{}\n");
                
                if (exp.responsibilities() != null && !exp.responsibilities().isEmpty()) {
                    expBuilder.append("  \\resumeItemListStart\n");
                    for (String resp : exp.responsibilities()) {
                        expBuilder.append("    \\resumeItem{").append(LatexEscapeUtil.escape(resp)).append("}\n");
                    }
                    expBuilder.append("  \\resumeItemListEnd\n");
                }
            }
            expBuilder.append("\\resumeSubHeadingListEnd\n");
        }
        result = result.replace("{{EXPERIENCE_SECTION}}", expBuilder.toString());

        // Projects List
        StringBuilder projBuilder = new StringBuilder();
        if (resume.projects() != null && !resume.projects().isEmpty()) {
            projBuilder.append("%-----------PROJECTS-----------\n");
            projBuilder.append("\\section{Projects}\n");
            projBuilder.append("\\resumeSubHeadingListStart\n");
            for (ResumeData.Project proj : resume.projects()) {
                String tech = String.join(", ", proj.technologies().stream().map(LatexEscapeUtil::escape).toList());
                projBuilder.append("\\resumeProjectHeading\n")
                        .append("  {\\textbf{").append(LatexEscapeUtil.escape(proj.name())).append("} $|$ \\emph{").append(tech).append("}}{}\n")
                        .append("  \\resumeItemListStart\n");
                if (proj.description() != null) {
                    for (String desc : proj.description()) {
                        projBuilder.append("    \\resumeItem{").append(LatexEscapeUtil.escape(desc)).append("}\n");
                    }
                }
                projBuilder.append("  \\resumeItemListEnd\n");
            }
            projBuilder.append("\\resumeSubHeadingListEnd\n");
        }
        result = result.replace("{{PROJECTS_SECTION}}", projBuilder.toString());

        // Education List
        StringBuilder eduBuilder = new StringBuilder();
        if (resume.education() != null && !resume.education().isEmpty()) {
            eduBuilder.append("%-----------EDUCATION-----------\n");
            eduBuilder.append("\\section{Education}\n");
            eduBuilder.append("\\resumeSubHeadingListStart\n");
            for (ResumeData.Education edu : resume.education()) {
                eduBuilder.append("\\resumeSubheading\n")
                        .append("  {").append(LatexEscapeUtil.escape(edu.institution())).append("}{}\n")
                        .append("  {").append(LatexEscapeUtil.escape(edu.degree())).append("}{").append(LatexEscapeUtil.escape(edu.year())).append("}\n");
            }
            eduBuilder.append("\\resumeSubHeadingListEnd\n");
        }
        result = result.replace("{{EDUCATION_SECTION}}", eduBuilder.toString());

        // Certifications List
        StringBuilder certBuilder = new StringBuilder();
        if (resume.certifications() != null && !resume.certifications().isEmpty()) {
            certBuilder.append("%-----------CERTIFICATIONS-----------\n");
            certBuilder.append("\\section{Certifications}\n");
            certBuilder.append("\\resumeSubHeadingListStart\n");
            for (String cert : resume.certifications()) {
                certBuilder.append("\\resumeProjectHeading{\\textbf{").append(LatexEscapeUtil.escape(cert)).append("}}{}\n");
            }
            certBuilder.append("\\resumeSubHeadingListEnd\n");
        }
        result = result.replace("{{CERTIFICATIONS_SECTION}}", certBuilder.toString());

        return result;
    }
}