package com.example.filepicker.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StyleRes;

/**
 * Created by prakh on 07-01-2018.
 */

public final class PreConditions {

    private PreConditions() {
    }

    public static <T> T checkNotNull(
            T val,
            String errorMessageTemplate,
            Object... errorMessageArgs) {
        if (val == null) {
            throw new NullPointerException(String.format(errorMessageTemplate, errorMessageArgs));
        }
        return val;
    }

    @StyleRes
    public static int checkValidStyle(
            Context context,
            int styleId,
            String errorMessageTemplate,
            Object... errorMessageArguments) {
        try {
            String resourceType = context.getResources().getResourceTypeName(styleId);
            if (!"style".equals(resourceType)) {
                throw new IllegalArgumentException(
                        String.format(errorMessageTemplate, errorMessageArguments));
            }
            return styleId;
        } catch (Resources.NotFoundException ex) {
            throw new IllegalArgumentException(
                    String.format(errorMessageTemplate, errorMessageArguments));
        }
    }
}
