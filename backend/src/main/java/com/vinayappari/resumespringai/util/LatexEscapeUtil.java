package com.vinayappari.resumespringai.util;

public class LatexEscapeUtil {

    public static String escape(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        // Replace backslash first to avoid double-escaping later
        String escaped = text.replace("\\", "\\textbackslash{}");

        // Escape special LaTeX characters
        escaped = escaped.replace("{", "\\{")
                .replace("}", "\\}")
                .replace("$", "\\$")
                .replace("&", "\\&")
                .replace("#", "\\#")
                .replace("^", "\\textasciicircum{}")
                .replace("_", "\\_")
                .replace("~", "\\textasciitilde{}")
                .replace("%", "\\%");

        return escaped;
    }
}