import { cn } from "../ui/utils";

const MIN_CHARS = 50;

interface Props {
  value: string;
  onChange: (v: string) => void;
  disabled?: boolean;
}

export function JobDescriptionInput({ value, onChange, disabled }: Props) {
  const tooShort = value.trim().length < MIN_CHARS;

  return (
    <div className="space-y-1.5">
      <div className="flex items-center justify-between">
        <label className="block text-xs font-semibold uppercase tracking-wider text-muted-foreground">
          Job Description
        </label>
        <span className={cn(
          "text-xs font-mono transition-colors",
          tooShort && value.length > 0 ? "text-amber-500" : "text-muted-foreground"
        )}>
          {value.length} chars
        </span>
      </div>

      <textarea
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        placeholder="Paste the full job description here — the more detail, the better the tailoring..."
        rows={9}
        className="w-full px-4 py-3 text-sm text-foreground placeholder-muted-foreground bg-muted/50 dark:bg-muted/20 border border-border rounded-xl resize-none outline-none transition-all duration-150 leading-relaxed focus:ring-2 focus:ring-indigo-500/25 focus:border-indigo-400 disabled:opacity-50 disabled:cursor-not-allowed"
      />

      {tooShort && value.length > 0 && (
        <p className="text-xs text-amber-500">
          Add more detail — at least {MIN_CHARS} characters recommended.
        </p>
      )}
    </div>
  );
}
