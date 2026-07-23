export interface ExperienceItem {
  title: string;
  company: string;
  duration: string;
  bullets: string[];
}

export interface AnalyzeResponse {
  fileId: string;
  summary: string;
  experience: ExperienceItem[];
  skills: Array<{ category: string; skills: string[] }>;
  oldScore?: number;
  newScore?: number;
  downloadUrl?: string;
}

export type AppPhase = "input" | "loading" | "results";
