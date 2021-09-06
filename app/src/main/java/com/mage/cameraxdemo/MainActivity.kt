package com.mage.cameraxdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.AnyThread
import androidx.camera.core.*
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.core.SurfaceRequest.TransformationInfo
import androidx.camera.core.SurfaceRequest.TransformationInfoListener
import androidx.camera.core.impl.CameraInternal
import androidx.camera.core.impl.utils.Threads
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            100
        )//请求权限
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)//获得provider实例
        tv_takepic.setOnClickListener {
//            val path = filesDir.absolutePath + File.separator + System.currentTimeMillis() + ".jpg"//使用内部存储存储最终图片
//            val path =
            var file= File(getExternalFilesDir(""),"faeFile")
            if(!file.exists()){
                file.mkdirs()
            }
            val photoFile = File(file, "${System.currentTimeMillis() }.jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()//构建输出选项
            var cameraExecutor = Executors.newSingleThreadExecutor()
            //点击拍照
            imageCapture.takePicture(outputFileOptions, cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(error: ImageCaptureException) {
                    }
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)//获取uri
                        println("拍照成功")
                        startActivity(Intent(this@MainActivity,ImageShowActivity::class.java).apply {//跳转到新页面展示图片
                            putExtra("path",savedUri)
                        })
                    }
                })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //默认你会同意权限，不同意就是自己的事了
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * 绑定预览view
     */
    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        var preview: Preview = Preview.Builder()
            .build()
        var cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)//前置摄像头：LENS_FACING_FRONT  后置摄像头：LENS_FACING_BACK
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
//        preview.setSurfaceProvider(mSurfaceProvider)
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
        var camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageCapture,//这个参数必须加上才能进行拍照
            preview
        )
    }


}