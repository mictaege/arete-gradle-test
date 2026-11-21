package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete.ScreenshotTaker

class UiDummyScreenshotTaker : ScreenshotTaker {

    override fun takeWhen(): Set<ScreenshotTaker.TestResult> =
        setOf(ScreenshotTaker.TestResult.SUCCESS, ScreenshotTaker.TestResult.FAILURE)

    override fun getImageBytes(): ByteArray =
        runCatching {
            javaClass.getResource("ui-dummy.png")?.readBytes()
        }.getOrDefault(ByteArray(0)) ?: ByteArray(0)
}
