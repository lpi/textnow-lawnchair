/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.sponsored;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.android.launcher3.util.ApiWrapper;
import com.android.launcher3.util.LauncherModelHelper;
import com.android.launcher3.util.PackageManagerHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SponsoredAppUtilsTest {

    private static final String PACKAGE_NAME = "com.test.sponsored";
    private static final String FALLBACK_URL = "https://play.google.com/store/apps/details?id=com.test.sponsored";

    private LauncherModelHelper mModelHelper;
    private Context mContext;
    private PackageManagerHelper mPmHelper;
    private ApiWrapper mApiWrapper;

    @Before
    public void setUp() {
        mModelHelper = new LauncherModelHelper();
        mContext = mModelHelper.sandboxContext;
        mPmHelper = mock(PackageManagerHelper.class);
        mApiWrapper = mock(ApiWrapper.class);
    }

    @After
    public void tearDown() {
        mModelHelper.destroy();
    }

    @Test
    public void getLaunchIntent_whenInstalled_returnsAppLaunchIntent() {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN).setPackage(PACKAGE_NAME);
        when(mPmHelper.getAppLaunchIntent(PACKAGE_NAME, Process.myUserHandle()))
                .thenReturn(launchIntent);

        Intent result = SponsoredAppUtils.getLaunchIntent(
                mContext,
                Process.myUserHandle(),
                PACKAGE_NAME,
                FALLBACK_URL,
                mPmHelper,
                mApiWrapper);

        assertSame(launchIntent, result);
    }

    @Test
    public void getLaunchIntent_whenMissing_usesFallbackUrl() {
        Intent result = SponsoredAppUtils.getLaunchIntent(
                mContext,
                Process.myUserHandle(),
                PACKAGE_NAME,
                FALLBACK_URL,
                mPmHelper,
                mApiWrapper);

        assertEquals(Intent.ACTION_VIEW, result.getAction());
        assertEquals(FALLBACK_URL, result.getDataString());
    }

    @Test
    public void getLaunchIntent_withoutFallback_usesMarketIntent() {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setPackage("com.android.vending");
        when(mApiWrapper.getAppMarketActivityIntent(PACKAGE_NAME, Process.myUserHandle()))
                .thenReturn(marketIntent);

        Intent result = SponsoredAppUtils.getLaunchIntent(
                mContext,
                Process.myUserHandle(),
                PACKAGE_NAME,
                null,
                mPmHelper,
                mApiWrapper);

        assertSame(marketIntent, result);
    }
}
