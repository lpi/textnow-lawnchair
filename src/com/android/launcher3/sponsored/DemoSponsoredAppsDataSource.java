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
                                context.getString(R.string.sponsored_iheartradio_title),
                                "com.clearchannel.iheartradio.controller",
                                "https://play.google.com/store/apps/details?id=com.clearchannel.iheartradio.controller",
                                R.drawable.ic_sponsored_iheartradio),
                        new SponsoredAppSpec(
                                context.getString(R.string.sponsored_accuweather_title),
                                "com.accuweather.android",
                                "https://play.google.com/store/apps/details?id=com.accuweather.android",
                                R.drawable.ic_sponsored_accuweather)));
    }
}
