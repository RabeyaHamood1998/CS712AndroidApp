package com.example.firstandroidapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Assignment6UiTest {

    private lateinit var device: UiDevice
    private val timeout = 5000L
    private val appName = "FirstAndroidApp"
    private val packageName = "com.example.firstandroidapp"

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
    }

    @Test
    fun testLaunchAppOpenSecondActivityAndCheckText() {
        val appIcon = findAppOnHomeScreen() ?: findAppInAppDrawer()

        assertTrue("App icon was not found on home screen or app drawer", appIcon != null)
        appIcon!!.click()

        handlePermissionPopup()

        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), timeout)

        val startButton = device.wait(
            Until.findObject(By.text("Start Activity Explicitly")),
            timeout
        )
        assertTrue("Start Activity Explicitly button not found", startButton != null)
        startButton!!.click()

        val challengeText = device.wait(
            Until.findObject(By.textContains("Device Fragmentation")),
            timeout
        )
        assertTrue("Challenge text not found on second activity", challengeText != null)
    }

    private fun findAppOnHomeScreen(): UiObject2? {
        return device.wait(
            Until.findObject(By.text(appName)),
            2000
        )
    }

    private fun findAppInAppDrawer(): UiObject2? {
        device.swipe(
            device.displayWidth / 2,
            device.displayHeight - 200,
            device.displayWidth / 2,
            200,
            20
        )

        return device.wait(
            Until.findObject(By.text(appName)),
            timeout
        )
    }

    private fun handlePermissionPopup() {
        val allowButtonById = device.wait(
            Until.findObject(By.res("com.android.permissioncontroller:id/permission_allow_button")),
            3000
        )

        if (allowButtonById != null) {
            allowButtonById.click()
            return
        }

        val allowForegroundOnlyButton = device.wait(
            Until.findObject(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button")),
            2000
        )

        if (allowForegroundOnlyButton != null) {
            allowForegroundOnlyButton.click()
            return
        }

        val allowOneTimeButton = device.wait(
            Until.findObject(By.res("com.android.permissioncontroller:id/permission_one_time_button")),
            2000
        )

        if (allowOneTimeButton != null) {
            allowOneTimeButton.click()
            return
        }

        val allowTextButton = device.wait(
            Until.findObject(By.textContains("Allow")),
            2000
        )

        if (allowTextButton != null) {
            allowTextButton.click()
        }
    }
}