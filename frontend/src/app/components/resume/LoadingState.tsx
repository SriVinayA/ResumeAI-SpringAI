import { Sparkles } from "lucide-react";
import { motion, AnimatePresence } from "motion/react";
import { useLoadingMessages } from "../../hooks/useLoadingMessages";

interface Props {
  active: boolean;
}

export function LoadingState({ active }: Props) {
  const { message, index, total } = useLoadingMessages(active);

  return (
    <div className="flex flex-col items-center justify-center gap-8 py-16 px-6 h-full">
      {/* Animated orb */}
      <div className="relative flex items-center justify-center">
        <div className="absolute w-24 h-24 rounded-full bg-gradient-to-br from-indigo-400 to-violet-500 opacity-20 animate-ping" />
        <div className="absolute w-20 h-20 rounded-full bg-gradient-to-br from-indigo-400 to-violet-500 opacity-10 scale-110 animate-pulse" />
        <div className="relative w-16 h-16 rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center shadow-lg shadow-indigo-500/30">
          <Sparkles className="w-7 h-7 text-white" style={{ animation: "spin 4s linear infinite" }} />
        </div>
      </div>

      {/* Rotating message */}
      <div className="text-center space-y-2" style={{ minHeight: "3.5rem" }}>
        <AnimatePresence mode="wait">
          <motion.p
            key={index}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.35, ease: [0.16, 1, 0.3, 1] }}
            className="text-sm font-semibold text-foreground"
          >
            {message}
          </motion.p>
        </AnimatePresence>
        <p className="text-xs text-muted-foreground">Usually takes 15–30 seconds</p>
      </div>

      {/* Step indicator */}
      <div className="flex items-center gap-2" aria-hidden>
        {Array.from({ length: total }).map((_, i) => {
          const done = i < index % total;
          const current = i === index % total;
          return (
            <span
              key={i}
              className={[
                "block rounded-full transition-all duration-300",
                current ? "w-4 h-1.5 bg-indigo-500" : "w-1.5 h-1.5",
                done ? "bg-indigo-400" : !current ? "bg-slate-200 dark:bg-slate-700" : "",
              ].join(" ")}
            />
          );
        })}
      </div>

      {/* Skeleton preview */}
      <div className="w-full max-w-xs space-y-2.5 mt-2">
        {[90, 72, 84, 55, 68].map((w, i) => (
          <div
            key={i}
            className="h-2 rounded-full bg-slate-100 dark:bg-slate-800 animate-pulse"
            style={{ width: `${w}%`, animationDelay: `${i * 0.12}s` }}
          />
        ))}
      </div>
    </div>
  );
}
