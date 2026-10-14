package com.greenslocks.iptv

import org.junit.Assert.*
import org.junit.Test

class UpdatePolicyTest {
    private val url = "https://github.com/jamarius997-a11y/Greens-Locks/releases/download/preview-latest/VanceTV-Preview.apk"

    @Test fun acceptsOnlyPreviewBuildTitlesAboveOldVersions() {
        assertEquals(10009, previewBuildNumber("Vance TV Preview build 10009"))
        assertNull(previewBuildNumber("Vance TV Preview build 42"))
        assertNull(previewBuildNumber("Vance TV Beta build 10009"))
        assertNull(previewBuildNumber("Vance TV Preview build 10009 extra"))
        assertNull(previewBuildNumber("Vance TV Preview build 99999999999999999"))
    }

    @Test fun pinsPreviewDownloadsToTheCorrectRepositoryAndAsset() {
        assertTrue(validPreviewDownload(url))
        assertFalse(validPreviewDownload(url.replace("https:", "http:")))
        assertFalse(validPreviewDownload(url.replace("github.com", "github.com.example.org")))
        assertFalse(validPreviewDownload(url.replace("jamarius997-a11y", "someone-else")))
        assertFalse(validPreviewDownload(url.replace("preview-latest", "beta-latest")))
        assertFalse(validPreviewDownload(url + "?redirect=elsewhere"))
        assertFalse(validPreviewDownload("not a URL"))
    }
}
