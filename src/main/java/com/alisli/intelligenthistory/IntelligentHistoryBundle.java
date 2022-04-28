package com.alisli.intelligenthistory;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public class IntelligentHistoryBundle {
    @NonNls
    private static final String PATH_TO_BUNDLE = "messages.IntelligentHistoryBundle";
    private static Reference<ResourceBundle> ourBundle;

    private IntelligentHistoryBundle() {
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = "messages.IntelligentHistoryBundle") String key, @NotNull Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(PATH_TO_BUNDLE);
            ourBundle = new SoftReference<>(bundle);
        }
        return bundle;
    }
}
