package com.android.launcher3.sponsored;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Access point for sponsored apps content.
 */
public final class SponsoredAppsRepository {

    private static final SponsoredAppsDataSource DATA_SOURCE = new DemoSponsoredAppsDataSource();

    private SponsoredAppsRepository() {
    }

    @NonNull
    public static SponsoredFolderSpec getSponsoredFolderSpec(@NonNull Context context) {
        return DATA_SOURCE.getSponsoredFolder(context);
    }
}
