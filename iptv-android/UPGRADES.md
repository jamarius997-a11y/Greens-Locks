# Vance TV Preview upgrades

## Sports Hub

Sports is available in the navigation rail and top navigation. On now and Coming up use provider/XMLTV guide listings; Sports channels remains usable without guide listings. Filter by sport, search program/team/channel text, or restrict to favorites. Select upcoming listings to toggle existing in-app reminders; hold OK on a card for channel options, including Add to multiview. The Multiview shortcut opens the existing player and connection-limit warning.

The hub prioritizes up to 120 visible sports and favorite channels with the existing six-request guide limit. Sports programs on other channels appear when their guide has already been loaded. It does not claim complete event coverage, verified fixtures, live scores, or that an airing is a live match: provider listings can include studio shows and replays. Reminders require the app to remain open. Hidden categories, locked categories, and kids category restrictions apply before guide requests and display.

The background is static and follows the current accent. Entering Sports does not start a stream. Selecting a card plays fullscreen and returns to the activated card with retained filters and scroll state.

## Suggested next releases

1. **Playback while browsing:** corner player, explicit keep-audio toggle, predictable Back behavior, and resume after closing multiview. Test decoder/surface changes and provider connection counts on TV hardware.
2. **Sports multiview:** choose games directly from the hub, focus tile for audio, swap/reorder tiles, and remember layouts. Respect plan stream limits.
3. **Playback recovery:** visible reconnect state and Retry, tune Fast start/Stability presets, retain language and subtitle preferences, and test stream alternatives.
4. **Background behavior:** explicit stop-on-exit versus optional picture-in-picture/audio, lifecycle handling, sleep-timer behavior, and low-power mode. Avoid starting playback on launch.
5. **Guide freshness and coverage:** timed cache expiry, retryable guide errors, scan-more control, and configurable sports-channel inclusion for unfamiliar networks.
6. **Verified sports data:** choose a licensed schedules/scores source, match events to provider channels, and add favorite teams, spoiler settings, and time-zone handling.
7. **Library:** improve series resume, watched state, favorite ordering, and channel hiding/renaming. Build on existing profiles rather than duplicating them.
8. **Backup and accessibility:** portable preferences without exposing provider credentials, larger text, stronger focus contrast, screen-reader labels, and device-tested remote paths.

## Device acceptance checks

- Start Preview and enter Sports without autoplay or audio beginning.
- Navigate every control using a D-pad, including long-press options; switch filters and return from fullscreen.
- Verify hidden, PIN-locked and kids-disallowed channels never appear.
- Try an account without EPG, failed connectivity, empty favorites, and a large provider catalog.
- Test current playback, upcoming reminders and cancellation, guide time shifts, and XMLTV fallback.
- Add two allowed channels to multiview and verify audio selection and plan warnings.
- Check small TV resolutions, long channel/program names, and reduced-motion settings.
