package com.mage.cameraxdemo

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

/**
 * author  :mayong
 * function:
 * date    :2021/5/31
 **/
class App : Application() ,CameraXConfig.Provider{
    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}