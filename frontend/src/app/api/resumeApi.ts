import type { AnalyzeResponse } from "../types/resume";

const BASE_URL = "http://localhost:8080";

export async function analyzeResume(
  resume: File,
  jobDescription: string
): Promise<AnalyzeResponse> {
  const form = new FormData();
  form.append("resume", resume);
  form.append("jobDescription", jobDescription.trim());

  const url = new URL(`${BASE_URL}/api/v1/resume/analyze`);

  const res = await fetch(url.toString(), { method: "POST", body: form });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Server error (${res.status})`);
  }

  return res.json() as Promise<AnalyzeResponse>;
}

export async function downloadResumePdf(fileId: string): Promise<Blob> {
  const res = await fetch(`${BASE_URL}/api/v1/resume/download/${fileId}`);

  if (!res.ok) {
    throw new Error(`Download failed (${res.status})`);
  }

  return res.blob();
}
