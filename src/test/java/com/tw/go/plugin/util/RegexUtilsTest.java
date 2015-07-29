package com.tw.go.plugin.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexUtilsTest {
    @Test
    public void shouldReturnTrueIfNoPatternProvided() {
        assertTrue(RegexUtils.matchesRegex("anything", null));
        assertTrue(RegexUtils.matchesRegex("anything", ""));
    }

    @Test
    public void shouldReturnTrueIfStringMatchesAnyPattern() {
        assertTrue(RegexUtils.matchesRegex("mail@gmail.com", ".*@gmail.com$"));
        assertTrue(RegexUtils.matchesRegex("mail@gmail.com", " .*@gmail.com$ "));
        assertTrue(RegexUtils.matchesRegex("mail@foo.co.in", ".*@foo.com$|.*@foo.co.in$"));
    }

    @Test
    public void shouldReturnFalseIfNoStringProvided() {
        assertFalse(RegexUtils.matchesRegex(null, ".*@gmail.com$"));
        assertFalse(RegexUtils.matchesRegex(" ", ".*@gmail.com$"));
    }

    @Test
    public void shouldReturnFalseIfStringDoesNotMatchAnyPattern() {
        assertFalse(RegexUtils.matchesRegex("mail@some.domain.com", ".*@gmail.com$"));
    }
}
