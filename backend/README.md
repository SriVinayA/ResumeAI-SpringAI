# ResumeSpringAI Backend

This is the backend for the AI-powered ATS resume tailor application. It parses uploaded resumes and job descriptions using Spring AI, rewrites the resume to aggressively match the job description, injects it into a LaTeX template, and compiles it into a PDF via Docker.

## Architecture & Design

The application follows a **Hexagonal / Domain-Driven Design (DDD)** architecture. This separates concerns clearly between the core business logic (orchestration and domain), data transfer objects (DTOs), and external integrations (infrastructure and controllers).

### Directory Structure

```text
com.vinayappari.resumespringai
├── application/       # Application Services (Use Cases)
│   └── ResumeOrchestratorService.java    # The main facade coordinating extraction, AI, and LaTeX generation
├── controller/        # Web Layer (REST APIs)
│   └── ResumeController.java             # Handles HTTP requests for uploading and downloading files
├── domain/            # Core Domain Interfaces & Business Rules
│   ├── AiAnalyzer.java                   # Interface for AI optimization operations
│   ├── DocumentExtractor.java            # Interface for extracting text from files
│   ├── FileStorage.java                  # Interface for saving and retrieving generated files
│   └── PdfGenerator.java                 # Interface for LaTeX -> PDF compilation
├── dto/               # Data Transfer Objects (Records)
│   ├── ExtractionResponse.java
│   ├── JobDescriptionData.java
│   ├── OptimizationResponse.java
│   └── ResumeData.java                   # Heavily nested record matching our custom JSON schema (includes SkillCategory)
├── exception/         # Global Exception Handling
│   └── GlobalExceptionHandler.java
├── infrastructure/    # External Integrations (Implementations of Domain Interfaces)
│   ├── AIService.java                    # Implements AiAnalyzer using Spring AI (OpenAI ChatClient)
│   ├── DocumentExtractionService.java    # Implements DocumentExtractor using Apache Tika
│   ├── LatexGeneratorService.java        # Implements PdfGenerator (Builds .tex, compiles PDF via Docker, generates Overleaf ZIP)
│   └── LocalFileStorage.java             # Implements FileStorage (Saves PDFs locally)
└── util/              # Utilities
    └── LatexEscapeUtil.java              # Escapes special characters for safe LaTeX injection
```

## How the Pipeline Works

1. **Extraction (`DocumentExtractionService`)**: The user uploads a Resume (PDF/Word) and a Job Description (PDF/Word/Text). Apache Tika extracts the raw text.
2. **AI Parsing & Optimization (`AIService`)**:
   - The raw text is passed to OpenAI via Spring AI.
   - We use the `parse-resume-system.st` prompt to structure the raw text into our `ResumeData` Java Record.
   - We use the `optimize-resume-system.st` prompt to aggressively tailor the parsed resume against the Job Description. The prompt enforces ATS best practices (keyword mirroring, context multiplication, CAR framework) and outputs the results as an `OptimizationResponse` (which contains the tailored `ResumeData` along with ATS scores and recommendations).
3. **LaTeX Generation (`LatexGeneratorService`)**:
   - The tailored `ResumeData` is injected into `src/main/resources/template.tex` using basic string replacements (and `LatexEscapeUtil` to prevent compilation errors).
   - The resulting `.tex` file is written to `src/main/resources/generated-resumes/`.
   - The service launches a Docker container (`texlive/texlive:latest`) to run `pdflatex` against the generated file, producing a PDF.
   - It also generates a Base64-encoded ZIP file containing both the generated `.tex` and our `resume.cls` class file. This Base64 payload is sent back to the frontend to power the "Open in Overleaf" feature.
4. **Delivery (`ResumeOrchestratorService`)**: The orchestrator ties this together and returns the generated PDF `fileId`, ATS analytics, and the Overleaf Base64 ZIP back to the `ResumeController` as a JSON map.

## Key Files & Customizations

- `src/main/resources/template.tex`: The base LaTeX template. Modify this to change the visual layout of the PDF.
- `src/main/resources/resume.cls`: The LaTeX class file defining styles and macros (like `\resumeSubheading`).
- `src/main/resources/prompts/optimize-resume-system.st`: The core AI instruction set. Modify this to tweak how aggressively the AI rewrites bullet points or what ATS rules it follows.
- `src/main/resources/application.properties`: Standard Spring Boot configurations (port, file size limits).

## Prerequisites

- **Java 21+**
- **Docker**: Must be installed and running. The backend relies on Docker to compile LaTeX locally via the `texlive/texlive:latest` image.
- **OpenAI API Key**: Required for Spring AI. Must be provided as an environment variable or in `application.properties`.

## Running the Application

Set your OpenAI API key in the environment:
```bash
export SPRING_AI_OPENAI_API_KEY="your-sk-key"
```

Start the application using Gradle:
```bash
./gradlew bootRun
```

The server will start on `http://localhost:8080`.
