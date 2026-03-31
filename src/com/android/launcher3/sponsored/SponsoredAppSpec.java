package com.android.launcher3.sponsored;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Definition for one sponsored app placeholder.
 */
public class SponsoredAppSpec {

    @NonNull
    private final String mTitle;
    @NonNull
    private final String mPackageName;
    @Nullable
    private final String mFallbackUrl;
    @DrawableRes
    private final int mIconRes;

    public SponsoredAppSpec(
            @NonNull String title,
            @NonNull String packageName,
            @Nullable String fallbackUrl,
            @DrawableRes int iconRes) {
        mTitle = title;
        mPackageName = packageName;
        mFallbackUrl = fallbackUrl;
        mIconRes = iconRes;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public String getPackageName() {
        return mPackageName;
    }

    @Nullable
    public String getFallbackUrl() {
        return mFallbackUrl;
    }

    @DrawableRes
    public int getIconRes() {
        return mIconRes;
    }
}
