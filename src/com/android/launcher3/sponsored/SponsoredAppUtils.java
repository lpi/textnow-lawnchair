package com.android.launcher3.sponsored;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.UserHandle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.ApiWrapper;
import com.android.launcher3.util.PackageManagerHelper;

/**
 * Helpers for sponsored app placeholders.
 */
public final class SponsoredAppUtils {

    public static final String ACTION = "com.android.launcher3.action.SPONSORED_APP";
    public static final String EXTRA_PACKAGE_NAME = "com.android.launcher3.extra.SPONSORED_PACKAGE";
    public static final String EXTRA_FALLBACK_URL = "com.android.launcher3.extra.SPONSORED_URL";

    private SponsoredAppUtils() {
    }

    @NonNull
    public static Intent createIntent(@NonNull String packageName, @Nullable String fallbackUrl) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        if (!TextUtils.isEmpty(fallbackUrl)) {
            intent.putExtra(EXTRA_FALLBACK_URL, fallbackUrl);
        }
        return intent;
    }

    public static boolean isSponsoredApp(int itemType, int options) {
        return itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT
                && (options & WorkspaceItemInfo.FLAG_SPONSORED_APP) != 0;
    }

    public static boolean isSponsoredApp(@Nullable ItemInfo itemInfo) {
        return itemInfo instanceof WorkspaceItemInfo
                && ((WorkspaceItemInfo) itemInfo).isSponsoredApp();
    }

    @Nullable
    public static String getTargetPackage(@Nullable Intent intent) {
        return intent == null ? null : intent.getStringExtra(EXTRA_PACKAGE_NAME);
    }

    @Nullable
    public static String getFallbackUrl(@Nullable Intent intent) {
        return intent == null ? null : intent.getStringExtra(EXTRA_FALLBACK_URL);
    }

    @Nullable
    public static Intent getLaunchIntent(
            @NonNull Context context, @NonNull WorkspaceItemInfo itemInfo) {
        return getLaunchIntent(
                context,
                itemInfo.user,
                getTargetPackage(itemInfo.getIntent()),
                getFallbackUrl(itemInfo.getIntent()),
                PackageManagerHelper.INSTANCE.get(context),
                ApiWrapper.INSTANCE.get(context));
    }

    @VisibleForTesting
    @Nullable
    static Intent getLaunchIntent(
            @NonNull Context context,
            @NonNull UserHandle user,
            @Nullable String packageName,
            @Nullable String fallbackUrl,
            @NonNull PackageManagerHelper pmHelper,
            @NonNull ApiWrapper apiWrapper) {
        if (!TextUtils.isEmpty(packageName)) {
            Intent launchIntent = pmHelper.getAppLaunchIntent(packageName, user);
            if (launchIntent != null) {
                return launchIntent;
            }
        }
        if (!TextUtils.isEmpty(fallbackUrl)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
        }
        if (!TextUtils.isEmpty(packageName)) {
            return apiWrapper.getAppMarketActivityIntent(packageName, user);
        }
        return null;
    }
}
