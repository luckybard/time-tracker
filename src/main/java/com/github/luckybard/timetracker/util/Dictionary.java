package com.github.luckybard.timetracker.util;

import com.intellij.DynamicBundle;

public class Dictionary extends DynamicBundle {

    private static final String BUNDLE = "dict.dictionary";

    public static final String COLON_WITH_SPACE = ": ";

    private Dictionary() {
        super(BUNDLE);
    }

    private static final Dictionary INSTANCE = new Dictionary();

    public static String translate(String key) {
        return INSTANCE.getMessage(key);
    }
}

