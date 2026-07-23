import { useCallback, useRef, useState } from "react";
import { Upload, FileText, X, CheckCircle2 } from "lucide-react";
import { cn } from "../ui/utils";

const ACCEPTED_MIME = [
  "application/pdf",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
];

interface Props {
  file: File | null;
  onChange: (file: File | null) => void;
  onError: (msg: string) => void;
}

export function FileUploader({ file, onChange, onError }: Props) {
  const [dragActive, setDragActive] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const validate = useCallback(
    (f: File) => {
      if (!ACCEPTED_MIME.includes(f.type)) {
        onError("Only PDF and DOCX files are supported.");
        return false;
      }
      if (f.size > 10 * 1024 * 1024) {
        onError("File exceeds the 10 MB limit.");
        return false;
      }
      return true;
    },
    [onError]
  );

  const handleDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      setDragActive(false);
      const dropped = e.dataTransfer.files[0];
      if (dropped && validate(dropped)) onChange(dropped);
    },
    [validate, onChange]
  );

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const f = e.target.files?.[0];
    if (f && validate(f)) onChange(f);
    // reset so the same file can be re-selected
    e.target.value = "";
  };

  if (file) {
    return (
      <div className="flex items-center gap-3 p-4 rounded-xl border border-emerald-200 dark:border-emerald-900/30 bg-emerald-50/60 dark:bg-emerald-900/20">
        <div className="shrink-0 w-9 h-9 rounded-lg bg-emerald-100 dark:bg-emerald-900/40 flex items-center justify-center">
          <FileText className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
        </div>

        <div className="flex-1 min-w-0">
          <p className="text-sm font-medium text-foreground truncate">{file.name}</p>
          <p className="flex items-center gap-1 mt-0.5 text-xs text-emerald-600">
            <CheckCircle2 className="w-3 h-3" />
            Ready · {(file.size / 1024).toFixed(0)} KB
          </p>
        </div>

        <button
          type="button"
          aria-label="Remove file"
          onClick={() => onChange(null)}
          className="shrink-0 p-1.5 rounded-lg text-muted-foreground hover:text-foreground hover:bg-emerald-100 dark:hover:bg-emerald-900/40 transition-colors"
        >
          <X className="w-4 h-4" />
        </button>
      </div>
    );
  }

  return (
    <div
      role="button"
      tabIndex={0}
      aria-label="Upload resume"
      onDragOver={(e) => { e.preventDefault(); setDragActive(true); }}
      onDragLeave={() => setDragActive(false)}
      onDrop={handleDrop}
      onClick={() => inputRef.current?.click()}
      onKeyDown={(e) => e.key === "Enter" && inputRef.current?.click()}
      className={cn(
        "flex flex-col items-center justify-center gap-3 p-8 rounded-xl border-2 border-dashed cursor-pointer select-none transition-all duration-200",
        dragActive
          ? "border-indigo-400 bg-indigo-50 dark:bg-indigo-900/20 scale-[1.01]"
          : "border-border bg-background hover:border-indigo-300 dark:hover:border-indigo-500/50 hover:bg-indigo-50/30 dark:hover:bg-indigo-900/20"
      )}
    >
      <input
        ref={inputRef}
        type="file"
        accept=".pdf,.docx"
        className="sr-only"
        onChange={handleInput}
      />

      <div className={cn(
        "w-12 h-12 rounded-xl flex items-center justify-center transition-colors duration-200",
        dragActive ? "bg-indigo-100 dark:bg-indigo-900/40" : "bg-muted"
      )}>
        <Upload className={cn("w-5 h-5 transition-colors", dragActive ? "text-indigo-500" : "text-muted-foreground")} />
      </div>

      <div className="text-center">
        <p className="text-sm font-medium text-foreground">
          Drop your resume or{" "}
          <span className="text-indigo-600 dark:text-indigo-400 underline underline-offset-2">browse files</span>
        </p>
        <p className="mt-1 text-xs text-muted-foreground">PDF or DOCX · Max 10 MB</p>
      </div>
    </div>
  );
}
