package com.android.launcher3.tests;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.test.runner.AndroidJUnitRunner;

import com.android.launcher3.testcomponent.TestLauncherActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Gradle-only instrumentation runner that bootstraps the test APK with the launcher capabilities
 * needed by shortcut-host tests.
 */
public class LauncherAndroidTestRunner extends AndroidJUnitRunner {

    private static final String HOME_ROLE = "android.app.role.HOME";

    private final List<String> mOriginalHomeRoleHolders = new ArrayList<>();

    @Override
    public void onStart() {
        prepareLauncherRole();
        super.onStart();
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        try {
            restoreLauncherRole();
        } finally {
            super.finish(resultCode, results);
        }
    }

    private void prepareLauncherRole() {
        Context testContext = getContext();
        enableTestLauncher(testContext, true);

        mOriginalHomeRoleHolders.clear();
        mOriginalHomeRoleHolders.addAll(getRoleHolders());

        String testPackage = testContext.getPackageName();
        if (!mOriginalHomeRoleHolders.contains(testPackage)) {
            runShellCommand("cmd role add-role-holder " + HOME_ROLE + " " + testPackage + " 0");
        }

        // Some APIs resolve launcher capability against the target app context instead.
        Context targetContext = getTargetContext();
        LauncherApps launcherApps = targetContext.getSystemService(LauncherApps.class);
        if (launcherApps != null && !launcherApps.hasShortcutHostPermission()) {
            String targetPackage = targetContext.getPackageName();
            if (!testPackage.equals(targetPackage)) {
                runShellCommand(
                        "cmd role add-role-holder " + HOME_ROLE + " " + targetPackage + " 0");
            }
        }
    }

    private void restoreLauncherRole() {
        List<String> currentHolders = getRoleHolders();
        if (!currentHolders.equals(mOriginalHomeRoleHolders)) {
            runShellCommand("cmd role clear-role-holders " + HOME_ROLE + " 0");
            for (String holder : mOriginalHomeRoleHolders) {
                runShellCommand("cmd role add-role-holder " + HOME_ROLE + " " + holder + " 0");
            }
        }

        enableTestLauncher(getContext(), false);
    }

    private void enableTestLauncher(@NonNull Context context, boolean enabled) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, TestLauncherActivity.class);
        packageManager.setComponentEnabledSetting(
                componentName,
                enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DEFAULT,
                DONT_KILL_APP);
    }

    @NonNull
    private List<String> getRoleHolders() {
        List<String> roleHolders = new ArrayList<>();
        String output = runShellCommand("cmd role get-role-holders " + HOME_ROLE);
        for (String line : output.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                roleHolders.add(trimmed);
            }
        }
        return roleHolders;
    }

    @NonNull
    private String runShellCommand(@NonNull String command) {
        try (ParcelFileDescriptor pfd = getUiAutomation().executeShellCommand(command);
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        new ParcelFileDescriptor.AutoCloseInputStream(pfd)))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (output.length() > 0) {
                    output.append('\n');
                }
                output.append(line);
            }
            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute shell command: " + command, e);
        }
    }
}
