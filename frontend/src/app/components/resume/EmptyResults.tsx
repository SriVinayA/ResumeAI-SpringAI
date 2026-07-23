import { Sparkles } from "lucide-react";

export function EmptyResults() {
  return (
    <div className="flex flex-col items-center justify-center gap-4 py-16 text-center h-full">
      <div className="w-14 h-14 rounded-2xl bg-indigo-50 dark:bg-indigo-900/30 border border-indigo-100/80 dark:border-indigo-800/50 flex items-center justify-center">
        <Sparkles className="w-6 h-6 text-indigo-400" />
      </div>

      <div>
        <p className="text-sm font-semibold text-foreground mb-1">Results appear here</p>
        <p className="text-xs text-muted-foreground leading-relaxed max-w-[200px]">
          Upload your resume and add a job description to begin.
        </p>
      </div>

      <div className="flex items-center gap-2 mt-2 text-muted-foreground/60">
        <div className="w-8 h-px bg-current" />
        <span className="text-[10px] font-mono tracking-widest uppercase">AI · Tailored · Export</span>
        <div className="w-8 h-px bg-current" />
      </div>
    </div>
  );
}
