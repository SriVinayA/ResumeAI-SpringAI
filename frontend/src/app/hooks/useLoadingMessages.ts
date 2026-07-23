import { useState, useEffect } from "react";

const MESSAGES = [
  "Analyzing your experience...",
  "Aligning with job requirements...",
  "Identifying key competencies...",
  "Optimizing keyword density...",
  "Crafting tailored bullet points...",
  "Generating professional PDF...",
];

const INTERVAL_MS = 2800;

export function useLoadingMessages(active: boolean) {
  const [index, setIndex] = useState(0);

  useEffect(() => {
    if (!active) {
      setIndex(0);
      return;
    }
    const id = setInterval(() => setIndex((i) => i + 1), INTERVAL_MS);
    return () => clearInterval(id);
  }, [active]);

  return {
    message: MESSAGES[index % MESSAGES.length],
    index,
    total: MESSAGES.length,
  };
}
