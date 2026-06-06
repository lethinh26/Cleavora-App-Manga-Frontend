package com.ptithcm.manga.util;

/**
 * Helper để format số chương.
 * - Số nguyên: "1.0" -> "1", "27.0" -> "27"
 * - Số thập phân: "1.5" -> "1.5", "10.5" -> "10.5"
 */
public class ChapterFormatter {

    public static String format(Double chapterNumber) {
        if (chapterNumber == null) return "";
        // Nếu là số nguyên (phần thập phân = 0) thì bỏ ".0"
        if (chapterNumber == Math.floor(chapterNumber) && !Double.isInfinite(chapterNumber)) {
            return String.valueOf(chapterNumber.intValue());
        }
        // Số thập phân: giữ nguyên nhưng bỏ trailing zero thừa
        String s = String.valueOf(chapterNumber);
        // "1.50" -> "1.5"
        if (s.contains(".")) {
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return s;
    }
}
