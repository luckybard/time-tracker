package com.github.luckybard.timetracker.util;

import com.intellij.BundleBase;

import java.util.ResourceBundle;

public class Dictionary {

    private static final String BUNDLE = "dict.dictionary";
    public static final String COLON_WITH_SPACE = ": ";

    public static String translate(String key) {
        return BundleBase.message(ResourceBundle.getBundle(BUNDLE), key);
    }
}

