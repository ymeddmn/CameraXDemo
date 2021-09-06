package com.mage.cameraxdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main2.*

class ImageShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        intent.getParcelableExtra<Uri>("path")?.let {
            val decodeFile = BitmapFactory.decodeFile(it.path)
            val height = (600f/decodeFile.width)*decodeFile.height
            val createScaledBitmap = Bitmap.createScaledBitmap(decodeFile, 600, height.toInt(), true)//这里必须进行压缩，我的手机上直接绘制根本绘制不出来这么大的图片
            iv.setImageBitmap(createScaledBitmap)
        }
    }
}