import { useState } from "react";
import { AnimatePresence, motion } from "motion/react";
import { Sparkles, AlertCircle } from "lucide-react";
import { toast, Toaster } from "sonner";

import { FileUploader } from "./components/resume/FileUploader";
import { JobDescriptionInput } from "./components/resume/JobDescriptionInput";
import { LoadingState } from "./components/resume/LoadingState";
import { ResultsPanel } from "./components/resume/ResultsPanel";
import { EmptyResults } from "./components/resume/EmptyResults";
import { Button } from "./components/ui/button";
import { Badge } from "./components/ui/badge";

import { ModeToggle } from "./components/mode-toggle";
import { analyzeResume, downloadResumePdf } from "./api/resumeApi";
import type { AnalyzeResponse, AppPhase } from "./types/resume";

const MIN_JD_LENGTH = 50;

export default function App() {
  const [phase, setPhase] = useState<AppPhase>("input");
  const [file, setFile] = useState<File | null>(null);
  const [jobDescription, setJobDescription] = useState("");
  const [results, setResults] = useState<AnalyzeResponse | null>(null);
  const [downloading, setDownloading] = useState(false);

  const canSubmit =
    phase === "input" &&
    !!file &&
    jobDescription.trim().length >= MIN_JD_LENGTH;

  // ── Handlers ─────────────────────────────────────────────────────────────────

  const handleSubmit = async () => {
    if (!canSubmit || !file) return;
    setPhase("loading");

    try {
      const data = await analyzeResume(file, jobDescription);
      setResults(data);
      setPhase("results");
      toast.success("Resume tailored successfully!");
    } catch (err) {
      const message = err instanceof Error ? err.message : "Something went wrong.";
      toast.error(message, {
        description: "Make sure the backend is running at localhost:8080 and try again.",
        icon: <AlertCircle className="w-4 h-4 text-red-500" />,
        duration: 6000,
      });
      setPhase("input");
    }
  };

  const handleDownload = async () => {
    if (!results?.fileId) return;
    setDownloading(true);
    try {
      const blob = await downloadResumePdf(results.fileId);
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "tailored-resume.pdf";
      a.click();
      URL.revokeObjectURL(url);
      toast.success("PDF downloaded!");
    } catch (err) {
      const message = err instanceof Error ? err.message : "Download failed.";
      toast.error(message);
    } finally {
      setDownloading(false);
    }
  };

  const handleReset = () => {
    setPhase("input");
    setFile(null);
    setJobDescription("");
    setResults(null);
  };

  // ── Render ────────────────────────────────────────────────────────────────────

  return (
    <>
      <Toaster
        position="top-right"
        toastOptions={{
          style: {
            fontFamily: "'DM Sans', sans-serif",
            fontSize: "13px",
            borderRadius: "12px",
          },
        }}
      />

      <div className="min-h-screen bg-background text-foreground" style={{ fontFamily: "'DM Sans', sans-serif" }}>
        {/* ── Header ── */}
        <header className="sticky top-0 z-30 border-b border-border bg-background/80 backdrop-blur-md">
          <div className="w-full mx-auto px-5 sm:px-8 h-14 flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-lg bg-indigo-600 flex items-center justify-center shadow-sm">
                <Sparkles className="w-3.5 h-3.5 text-white" />
              </div>
              <span
                className="text-sm font-bold text-foreground tracking-tight"
                style={{ fontFamily: "'Sora', sans-serif" }}
              >
                ResumeAI
              </span>
            </div>

            <div className="flex items-center gap-3">
              <Badge variant="secondary" className="text-indigo-600 bg-indigo-50 border-indigo-100 text-[11px] dark:bg-indigo-900/30 dark:border-indigo-800/50 dark:text-indigo-300">
                Beta
              </Badge>
              <ModeToggle />
            </div>
          </div>
        </header>



        {/* ── Split layout ── */}
        <main className="w-full mx-auto px-5 sm:px-8 py-8 md:py-10">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 lg:gap-8 items-start">

            {/* ── Left: Input Panel ── */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.45, delay: 0.1, ease: [0.16, 1, 0.3, 1] }}
              className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden"
            >
              <div className="px-6 py-4 border-b border-border flex items-center justify-between">
                <div>
                  <h2
                    className="text-sm font-bold text-foreground"
                    style={{ fontFamily: "'Sora', sans-serif" }}
                  >
                    Your Inputs
                  </h2>
                  <p className="text-xs text-muted-foreground mt-0.5">Resume · Job description</p>
                </div>
                <StepIndicator step={phase === "input" ? 1 : 2} />
              </div>

              <div className="p-6 space-y-5">
                {/* Upload */}
                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                    Resume
                  </label>
                  <FileUploader
                    file={file}
                    onChange={setFile}
                    onError={(msg) => toast.error(msg)}
                  />
                </div>

                {/* Job description */}
                <JobDescriptionInput
                  value={jobDescription}
                  onChange={setJobDescription}
                  disabled={phase === "loading"}
                />

                {/* Submit */}
                <Button
                  onClick={handleSubmit}
                  disabled={!canSubmit || phase === "loading"}
                  size="lg"
                  className={[
                    "w-full text-sm font-semibold transition-all duration-200",
                    canSubmit && phase !== "loading"
                      ? "bg-indigo-600 hover:bg-indigo-700 text-white shadow-lg shadow-indigo-500/20 hover:shadow-indigo-500/30 hover:scale-[1.01] active:scale-[0.99]"
                      : "bg-muted text-muted-foreground hover:bg-muted shadow-none",
                  ].join(" ")}
                >
                  {phase === "loading" ? (
                    <>
                      <div className="w-4 h-4 border-2 border-slate-300 border-t-slate-600 rounded-full animate-spin" />
                      Processing…
                    </>
                  ) : (
                    <>
                      <Sparkles className="w-4 h-4" />
                      Tailor My Resume
                    </>
                  )}
                </Button>

                {/* Helper hint */}
                {!canSubmit && phase === "input" && (
                  <p className="text-xs text-center text-muted-foreground">
                    {!file
                      ? "Upload your resume to continue"
                      : `Add at least ${MIN_JD_LENGTH} characters to the job description`}
                  </p>
                )}
              </div>
            </motion.div>

            {/* ── Right: Results Panel ── */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.45, delay: 0.2, ease: [0.16, 1, 0.3, 1] }}
              className={[
                "bg-card rounded-2xl border shadow-sm overflow-hidden lg:col-span-2",
                phase === "results" ? "border-indigo-500/50" : "border-border",
              ].join(" ")}
            >
              <div className="px-6 py-4 border-b border-border flex items-center justify-between">
                <div>
                  <h2
                    className="text-sm font-bold text-foreground"
                    style={{ fontFamily: "'Sora', sans-serif" }}
                  >
                    Tailored Results
                  </h2>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    {phase === "results"
                      ? "Review · Download"
                      : phase === "loading"
                      ? "AI is working…"
                      : "Awaiting submission"}
                  </p>
                </div>

                {phase === "results" && (
                  <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ type: "spring", stiffness: 300 }}>
                    <Badge className="bg-emerald-50 text-emerald-700 border-emerald-200 text-[11px]">
                      Complete
                    </Badge>
                  </motion.div>
                )}
              </div>

              <div className="p-6 min-h-[460px] overflow-y-auto">
                <AnimatePresence mode="wait">
                  {phase === "input" && (
                    <motion.div key="empty" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                      <EmptyResults />
                    </motion.div>
                  )}

                  {phase === "loading" && (
                    <motion.div key="loading" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                      <LoadingState active={phase === "loading"} />
                    </motion.div>
                  )}

                  {phase === "results" && results && (
                    <motion.div key="results" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
                      <ResultsPanel
                        data={results}
                        onDownload={handleDownload}
                        onReset={handleReset}
                        downloading={downloading}
                      />
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </motion.div>
          </div>

          <p className="text-center text-[11px] font-mono text-muted-foreground mt-10">
            Your data is processed securely and never stored permanently.
          </p>
        </main>
      </div>
    </>
  );
}

// ── Step indicator pill ───────────────────────────────────────────────────────

function StepIndicator({ step }: { step: 1 | 2 }) {
  return (
    <div className="flex items-center gap-1.5">
      {[1, 2].map((n) => (
        <span
          key={n}
          className={[
            "flex items-center justify-center w-5 h-5 rounded-full text-[10px] font-bold transition-colors duration-300",
            n === step
              ? "bg-indigo-600 text-white"
              : n < step
              ? "bg-emerald-100 text-emerald-600 dark:bg-emerald-900/30 dark:text-emerald-400"
              : "bg-muted text-muted-foreground",
          ].join(" ")}
        >
          {n}
        </span>
      ))}
    </div>
  );
}
