import { Download, RotateCcw, User, Briefcase, Code2, ChevronRight, CheckCircle2 } from "lucide-react";
import { motion } from "motion/react";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import type { AnalyzeResponse } from "../../types/resume";

interface Props {
  data: AnalyzeResponse;
  onDownload: () => void;
  onReset: () => void;
  downloading: boolean;
}

export function ResultsPanel({ data, onDownload, onReset, downloading }: Props) {
  return (
    <motion.div
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.5, ease: [0.16, 1, 0.3, 1] }}
      className="flex flex-col gap-4 h-full"
    >
      {/* Header */}
      <div className="flex items-start justify-between gap-4">
        <div>
          <div className="flex items-center gap-1.5 mb-1">
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500" />
            <span className="text-xs font-semibold uppercase tracking-wider text-emerald-600">
              Tailored Successfully
            </span>
          </div>
          <h2 className="text-lg font-bold text-foreground" style={{ fontFamily: "'Sora', sans-serif" }}>
            Your AI-Crafted Resume
          </h2>
        </div>

        {(data.oldScore !== undefined && data.newScore !== undefined) && (
          <div className="shrink-0 text-right">
            <p className="text-2xl font-bold text-indigo-500 leading-none" style={{ fontFamily: "'Sora', sans-serif" }}>
              {data.oldScore}% &rarr; {data.newScore}%
            </p>
            <p className="text-xs font-mono text-muted-foreground mt-0.5">ATS Score</p>
          </div>
        )}
      </div>

      {/* PDF Preview */}
      {data.fileId && (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.05, ease: [0.16, 1, 0.3, 1] }}
          className="flex-1 min-h-[600px] w-full rounded-xl overflow-hidden border border-border bg-muted/20 relative"
        >
          <iframe 
            src={`http://localhost:8080/api/v1/resume/download/${data.fileId}#view=FitH`}
            className="absolute inset-0 w-full h-full border-none"
            title="Resume PDF Preview"
          />
        </motion.div>
      )}

      {/* Actions */}
      <div className="flex gap-3 mt-auto pt-2">
        <Button
          onClick={onDownload}
          disabled={downloading}
          className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white shadow-lg shadow-indigo-500/20 hover:shadow-indigo-500/30 transition-all"
          size="lg"
        >
          {downloading ? (
            <>
              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              Preparing...
            </>
          ) : (
            <>
              <Download className="w-4 h-4" />
              Download PDF
            </>
          )}
        </Button>

        <Button
          onClick={onReset}
          variant="outline"
          size="lg"
          className="gap-2"
        >
          <RotateCcw className="w-4 h-4" />
          Start Over
        </Button>
      </div>
    </motion.div>
  );
}


