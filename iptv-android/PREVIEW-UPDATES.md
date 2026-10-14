# Preview auto-updates

Preview checks for updates on launch and in Settings → Check for updates. It reads only this repository's `preview-latest` prerelease, downloads the Preview APK, verifies its SHA-256, package name, version code and signing certificate, then opens Android's installer. Android requires the user to confirm installation and may require Allow from this source. The app does not install silently.

## Signing setup

Set these repository Actions secrets at https://github.com/jamarius997-a11y/Greens-Locks/settings/secrets/actions:

- `VANCETV_PREVIEW_KEYSTORE_BASE64`: Base64 of the persistent signing keystore.
- `VANCETV_PREVIEW_STORE_PASSWORD`: Keystore password.
- `VANCETV_PREVIEW_KEY_ALIAS`: Signing key alias.
- `VANCETV_PREVIEW_KEY_PASSWORD`: Key password.

The existing `VANCETV_KEYSTORE_BASE64`, `VANCETV_STORE_PASSWORD`, `VANCETV_KEY_ALIAS` and `VANCETV_KEY_PASSWORD` secrets can be used as a fallback if already configured. Use all four secrets from the same key. Keep the private keystore and its passwords backed up outside GitHub; never commit them or paste them into chat.

If the signing key for Preview 42 is available, use that key to preserve in-place updates and saved data. Otherwise, generate a new persistent key on your own computer and perform a one-time reinstall. The previous Preview's temporary key cannot be reconstructed from its APK.

Example key creation (keytool prompts for passwords):

```sh
keytool -genkeypair -keystore vancetv-preview.jks -alias vancetv-preview -keyalg RSA -keysize 3072 -validity 10000
```

Encode the keystore for the BASE64 secret:

```sh
# Linux/macOS
base64 < vancetv-preview.jks | tr -d '\n'
```

```powershell
# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes((Resolve-Path .\vancetv-preview.jks)))
```

Paste the encoded value directly into GitHub's secret form. Set KEY_ALIAS to `vancetv-preview` and the password secrets to the passwords chosen during key creation.

## Publication

Pushes or workflow-dispatch builds on `codex/sports-hub-preview` and `quiet-home-preview` in this repository run unit tests and build Preview. Preview version codes are `10000 + CI run number`, above the previous builds 3 and 42. Publishing refuses to proceed without a persistent signing key; unsigned development artifacts are not promoted to the update feed.

The workflow creates an immutable `preview-build-N` prerelease and updates the `preview-latest` prerelease with `VanceTV-Preview.apk`. Stable and Beta use their existing update repositories and tags. Pull requests never publish.

After setup, install the first signed Preview manually from the new feed. Older Preview builds do not contain this updater. Subsequent signed Preview releases can update in place while retaining settings.

Direct download once the first signed release exists:

`https://github.com/jamarius997-a11y/Greens-Locks/releases/download/preview-latest/VanceTV-Preview.apk`

## Device verification

Install signed build N, then publish signed build N+1. Confirm the launch prompt, Not now, manual check, download, source permission, install confirmation, and retained login/favorites. Confirm mismatched-channel, corrupted, older, and differently signed APKs are rejected. Test both Android TV and Fire TV before treating device behavior as verified.
