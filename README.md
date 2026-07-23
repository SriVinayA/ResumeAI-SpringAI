# ResumeAI: AI-Powered ATS Optimization Engine

ResumeAI is a modern, full-stack web application designed to automatically tailor any candidate's resume to a specific job description. By leveraging Spring AI, advanced algorithmic reverse-engineering, and dynamic LaTeX compilation, this tool guarantees a resume optimized for high Applicant Tracking System (ATS) parse fidelity and keyword matching.

## 🚀 Key Features

- **Aggressive ATS Optimization**: The AI agent is configured with strict instructions to aggressively reframe, extrapolate, and align the candidate's core experience to guarantee an ATS score of 95+.
- **Algorithmic Reverse-Engineering**:
  - **Exact-Match Mirroring**: Overcomes legacy Boolean ATS filters by perfectly mirroring nouns, tools, and acronyms from the Job Description.
  - **Placement Multipliers**: Strategically places keywords in exactly 3-4 contexts (Summary, Skills section, and Experience bullets) to maximize algorithmic weighting.
  - **Recency Bias**: Prioritizes matching skills into the most recent employment history.
  - **CAR Methodology**: Rewrites experience bullets using the Challenge-Action-Result framework for measurable impact.
- **Categorized Skills Engine**: Automatically categorizes raw technical skills into structured groups (e.g., Languages, Frameworks, Cloud) for highly readable formatting.
- **Dynamic PDF Generation**: Uses a robust LaTeX template compiled via a Dockerized TeX Live environment to ensure perfect, single-column parse fidelity without formatting errors.
- **Modern Fluid UI**: A responsive, dark-mode optimized React frontend featuring an interactive split-layout with a live PDF preview.

## 🛠 Tech Stack

**Frontend:**
- React 19 & Vite
- TypeScript
- Tailwind CSS v4 (Fluid layout, dynamic Dark Mode)
- Framer Motion & Lucide Icons

**Backend:**
- Java 21
- Spring Boot 3.4
- Spring AI (Structured Output, Prompt Resources)
- Docker (for executing `pdflatex` compilation safely)

## 🧠 System Architecture

1. **Input & Extraction**: The user uploads a resume (PDF/DOCX) and a Job Description. The backend extracts the raw text.
2. **Semantic Parsing**: Spring AI parses the unstructured text into strongly-typed Java Records (`ResumeData`, `JobDescriptionData`).
3. **AI Optimization**: The `optimize-resume-system.st` prompt forces the LLM to rewrite and tailor the structured resume data based on strict ATS heuristics.
4. **LaTeX Compilation**: The updated `ResumeData` is injected into a `template.tex` file, which natively supports dynamic skill categorization and bullet mapping.
5. **PDF Delivery**: A Docker process executes `pdflatex` to securely compile the `.tex` file into a pristine PDF, which is streamed back to the frontend iframe.

## ⚙️ Local Development Setup

### Prerequisites
- Node.js 20+
- Java 21+
- Docker (Must be running for PDF compilation)

### Starting the Backend
1. Navigate to the backend directory:
   ```bash
   cd Backend
   ```
2. Run the Spring Boot application using Gradle:
   ```bash
   ./gradlew bootRun
   ```
   *The server will start on `http://localhost:8080`.*

### Starting the Frontend
1. Navigate to the frontend directory:
   ```bash
   cd Frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The UI will be available at `http://localhost:5173`.*

## 📁 Prompt Engineering Configuration
All AI instructions have been abstracted out of the source code. You can fine-tune the AI's behavior by modifying the StringTemplate (`.st`) files located in `Backend/src/main/resources/prompts/`.
