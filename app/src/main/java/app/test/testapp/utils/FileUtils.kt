package app.test.testapp.utils

import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.net.Uri
import app.test.testapp.Injector
import java.io.IOException


object FileUtils {
    fun saveBitmap(bitmap: Bitmap): Uri{
        val fileName = generateFileName()
        try{
            val fos = Injector.context.openFileOutput(fileName, MODE_PRIVATE)
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
        return Uri.fromFile(Injector.context.getFileStreamPath(fileName))
    }
}