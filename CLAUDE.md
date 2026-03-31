# CLAUDE.md

## Quick Reference (Build → Install → Verify)

```bash
./gradlew compileDebugKotlin                        # fast compilation check
./gradlew spotlessApply                             # auto-format code
./gradlew spotlessCheck                             # verify formatting
./gradlew installLawnWithQuickstepGithubDebug       # build + install to device/emulator
adb shell am start -n app.lawnchair.debug/app.lawnchair.LawnchairLauncher  # launch app
adb logcat -s TextNowSessionProvider,ApiStatusWidgetDataSrc,LawnchairApp   # filtered logs
```

## Common Pitfalls

- **Submodule not initialized**: run `git submodule update --init` if you see errors about `searchuilib` or `iconloaderlib`
- **Signature mismatch on install**: run `adb uninstall app.lawnchair.debug` first, then reinstall
- **Ambiguous gradle task**: always use full variant name — `installLawnWithQuickstepGithubDebug`, NOT `installLawnWithQuickstepDebug`
- **App package ID** (debug): `app.lawnchair.debug`

## Project Architecture

```
lawnchair/src/app/lawnchair/     ← Lawnchair + TextNow custom code (PUT NEW CODE HERE)
src/com/android/launcher3/      ← AOSP Launcher3 clone (minimize changes)
quickstep/                      ← Recents/QuickStep integration
compatLib/                      ← Android version compat layers (V10–V15)
lawnchair/wire-protos/          ← Protocol buffer definitions for gRPC services
lawnchair/res/                  ← Lawnchair-specific resources (layouts, drawables, strings)
```

## TextNow Integration

### Auth Flow

All files in `lawnchair/src/app/lawnchair/auth/`:

1. **TextNowSessionProvider.kt** — retrieves session from TextNow app's content provider, or uses `DEBUG_SESSION_ID` override for testing
2. **TextNowSessionInterceptor.kt** — OkHttp interceptor that injects headers on every API request: `client_id`, `tn-session-id`, `tn-user-agent`, `client_type`, `tn-request-id`
3. **TextNowApiClient.kt** — singleton Retrofit + gRPC client, base URL: `https://api.prod.textnow.me/`
4. **TextNowSubscriptionService.kt** — REST endpoint: `wireless/subscriptions/v4/active_user_subscription`
5. **TextNowSessionContract.kt** — content provider contract constants (authority: `com.enflick.android.TextNow.launchersession`)
6. **TextNowSession.kt** — data class: sessionId, username, guid

### Status Widget

UI components in `lawnchair/src/app/lawnchair/statuswidget/`:
- **TextNowStatusWidgetContainer.kt** — home screen widget (data usage, loyalty points, offers count)
- **TextNowOffersSheet.kt** — Compose bottom sheet showing quests, rewards, and offers

Data layer in `src/com/android/launcher3/statuswidget/`:
- **StatusWidgetRepository.kt** → **ApiStatusWidgetDataSource.kt** → API calls
- **DemoStatusWidgetDataSource.kt** — offline fallback with fake data (auto-used on API errors)
- Data models: StatusWidgetData.kt, OfferItem.kt, QuestItem.kt, RewardItem.kt

Proto: `lawnchair/wire-protos/textnow/api/loyalty/v1/loyalty.proto` — LoyaltyService gRPC (GetBalance, GetLoyaltyInfo)

Smartspace: `SmartspaceMode.kt` has `TextNowStatus` mode, set as default in `lawnchair/res/values/config.xml`

### Debug Testing Without TextNow App

**Preferred**: Install the TextNow APK on the emulator and sign in. The content provider will supply a valid mobile session automatically.

**Manual override** (limited — bot defense blocks most direct API calls):
1. Log into textnow.com in a browser, get the `connect.sid` cookie
2. Fetch the real session ID: `curl -b "connect.sid=<value>" https://www.textnow.com/api/<username>/session` → use the `id` field
3. Set `DEBUG_SESSION_ID` in `TextNowSessionProvider.kt` to that value
4. Note: direct API calls may still fail due to PerimeterX bot defense; the TextNow app provides sessions with proper bot defense tokens

If no session is available, `ApiStatusWidgetDataSource` falls back to `DemoStatusWidgetDataSource` automatically.

## Code Conventions

- **Commits**: conventional commits — `type(scope): subject` (types: `feat`, `fix`, `style`, `refactor`, `perf`, `docs`, `test`, `chore`)
- **Formatting**: always run `./gradlew spotlessApply` before committing (Spotless + ktlint)
- **New TextNow code** goes in `lawnchair/src/app/lawnchair/` (auth/, statuswidget/, etc.)
- **Launcher3 modifications** go in `src/com/android/launcher3/` — keep these minimal
- **String naming**: see CONTRIBUTING.md for the naming convention table

## Verification Checklist

After every change, run through this loop:

1. `./gradlew compileDebugKotlin` — catches compilation errors fast
2. `./gradlew spotlessApply` — auto-fix formatting
3. `./gradlew installLawnWithQuickstepGithubDebug` — build + install
4. `adb shell am start -n app.lawnchair.debug/app.lawnchair.LawnchairLauncher` — launch
5. `adb logcat -d | grep -iE "FATAL|AndroidRuntime|TextNow|StatusWidget"` — check for crashes/errors
