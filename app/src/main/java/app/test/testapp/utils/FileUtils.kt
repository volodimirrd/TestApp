package app.test.testapp.utils

import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import android.net.Uri
import app.test.testapp.TestAppApplication
import java.io.FileOutputStream
import java.io.IOException


object FileUtils {
    fun saveBitmap(bitmap: Bitmap): Uri{
        val fileName = generateFileName()
        try{
            val fos = TestAppApplication.instance.openFileOutput(fileName, MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: IOException){
            e.printStackTrace()
        }
        return getFileUri(fileName)
    }

    private fun generateFileName(): String{
        return "photo_" + System.currentTimeMillis()
    }

    private fun getFileUri(fileName: String): Uri{
        return Uri.fromFile(TestAppApplication.instance.getFileStreamPath(fileName))
    }
}