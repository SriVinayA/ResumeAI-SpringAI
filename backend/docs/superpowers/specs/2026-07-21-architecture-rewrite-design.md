# Architecture Rewrite Design

## Goal
Rewrite the ResumeSpringAI codebase to be clean, neat, and adhere to SOLID design principles while avoiding over-engineering (YAGNI).

## Current Issues
- `ResumeController` manages internal state (`pdfStore`) and orchestrates too much business logic.
- Services are tightly coupled to their concrete implementations (e.g., Tika, DeepSeek, Docker-based pdflatex).
- `LatexGeneratorService` uses error-prone string replacement instead of a robust templating engine or cleanly isolated builder.
- The layers are blurred.

## Proposed Architecture: Clean Layered Architecture
We will implement a clean layered approach with explicit interfaces (Dependency Inversion).

### 1. API Layer (Controllers)
- `ResumeController`: Sole responsibility is handling incoming HTTP requests, validating multipart files/strings, delegating to the application layer, and returning HTTP responses.

### 2. Application Layer (Orchestration)
- `ResumeOrchestratorService`: New service that coordinates the flow:
  1. Extract text from uploaded document.
  2. Parse resume text and JD text into structured data.
  3. Optimize resume against JD.
  4. Generate PDF.
  5. Store PDF and return tracking ID.

### 3. Domain Layer (Interfaces & Models)
- **Models**: `ResumeData`, `JobDescriptionData`, `OptimizationResponse`.
- **Interfaces**:
  - `DocumentExtractor` (extracts text from `MultipartFile` or `InputStream`).
  - `AiAnalyzer` (parses resumes/JDs and performs optimization).
  - `PdfGenerator` (takes structured data and returns a generated PDF).
  - `FileStorage` (stores and retrieves generated PDFs).

### 4. Infrastructure Layer (Implementations)
- `TikaDocumentExtractor` implements `DocumentExtractor`.
- `DeepSeekAiAnalyzer` implements `AiAnalyzer`.
- `LatexPdfGenerator` implements `PdfGenerator`.
- `LocalFileStorage` implements `FileStorage`.

## Refactoring Steps
1. Define interfaces and move existing DTOs to a clear domain structure.
2. Extract the `pdfStore` from the controller into `LocalFileStorage`.
3. Refactor `DocumentExtractionService` to implement `DocumentExtractor`.
4. Refactor `AIService` to implement `AiAnalyzer`.
5. Refactor `LatexGeneratorService` to implement `PdfGenerator`. (Ideally, we could use a templating engine like FreeMarker for the `.tex` file if time permits, but basic cleanup of the string replacements is the minimum).
6. Create `ResumeOrchestratorService`.
7. Wire it all up in the `ResumeController`.

## Testing Strategy
- The interface separation will allow for easy unit testing of the orchestrator using mocks.
