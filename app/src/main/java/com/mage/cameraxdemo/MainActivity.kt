package com.mage.cameraxdemo

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.android.camera.utils.YuvToRgbConverter
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Runnable
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    var scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(Manifest.permission.CAMERA),
            100
        )//请求权限
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)//获得provider实例

    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //默认你会同意权限，不同意就是自己的事了

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(600, 600))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        var executor = Executors.newFixedThreadPool(5)
        imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { image ->
            scope.launch(Dispatchers.IO) {
                val bitmap = Bitmap.createBitmap(image.width,image.height,Bitmap.Config.ARGB_8888)
                YuvToRgbConverter(this@MainActivity).yuvToRgb(image = image.image!!,bitmap)//将image转化为bitmap，参考：https://github.com/android/camera-samples/blob/3730442b49189f76a1083a98f3acf3f5f09222a3/CameraUtils/lib/src/main/java/com/example/android/camera/utils/YuvToRgbConverter.kt
                image.close()//这里调用了close就会继续生成下一帧图片
                withContext(Dispatchers.Main){//这里更新ui会崩溃，搞不懂为啥，很郁闷
                    iv.setImageBitmap(bitmap)//回到主线程更新ui
                }
            }

        })
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))

    }

    /**
     * 绑定预览view
     */
    fun bindPreview(cameraProvider: ProcessCameraProvider, imageAnalysis: ImageAnalysis) {
        var preview: Preview = Preview.Builder()
            .build()

        var cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.getSurfaceProvider())

        var camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}