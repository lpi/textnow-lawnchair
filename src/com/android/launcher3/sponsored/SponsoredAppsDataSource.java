package com.android.launcher3.sponsored;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Source for carrier-sponsored apps content.
 */
public interface SponsoredAppsDataSource {

    @NonNull
    SponsoredFolderSpec getSponsoredFolder(@NonNull Context context);
}
