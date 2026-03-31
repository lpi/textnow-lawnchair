package com.android.launcher3.sponsored;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Definition for the sponsored folder seeded on the workspace.
 */
public class SponsoredFolderSpec {

    @NonNull
    private final String mTitle;
    @NonNull
    private final List<SponsoredAppSpec> mApps;

    public SponsoredFolderSpec(@NonNull String title, @NonNull List<SponsoredAppSpec> apps) {
        mTitle = title;
        mApps = Collections.unmodifiableList(apps);
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public List<SponsoredAppSpec> getApps() {
        return mApps;
    }
}
