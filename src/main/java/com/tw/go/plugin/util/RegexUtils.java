package com.tw.go.plugin.util;

import static org.apache.commons.lang.StringUtils.isBlank;

public class RegexUtils {
    public static boolean matchesRegex(String str, String pattern) {
        if (isBlank(pattern)) {
            return true;
        }
        try {
            if (str.matches(pattern.trim())) {
                return true;
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }
}
