package com.android.launcher3.sponsored;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.launcher3.R;

import java.util.Arrays;

/**
 * Demo-only sponsored apps source until API-backed sync is added.
 */
public class DemoSponsoredAppsDataSource implements SponsoredAppsDataSource {

    @NonNull
    @Override
    public SponsoredFolderSpec getSponsoredFolder(@NonNull Context context) {
        return new SponsoredFolderSpec(
                context.getString(R.string.sponsored_apps_folder_title),
                Arrays.asList(
                        new SponsoredAppSpec(
                                context.getString(R.string.sponsored_spotify_title),
                                "com.spotify.music",
                                "https://play.google.com/store/apps/details?id=com.spotify.music",
                                R.drawable.ic_sponsored_spotify),
                        new SponsoredAppSpec(
                                context.getString(R.string.sponsored_netflix_title),
                                "com.netflix.mediaclient",
                                "https://play.google.com/store/apps/details?id=com.netflix.mediaclient",
                                R.drawable.ic_sponsored_netflix),
                        new SponsoredAppSpec(
                                context.getString(R.string.sponsored_uber_title),
                                "com.ubercab",
                                "https://play.google.com/store/apps/details?id=com.ubercab",
                                R.drawable.ic_sponsored_uber)));
    }
}
