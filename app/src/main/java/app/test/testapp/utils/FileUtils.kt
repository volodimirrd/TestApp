package app.test.testapp.utils

import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.net.Uri
import app.test.testapp.Injector
import java.io.FileOutputStream
import java.io.IOException


object FileUtils {
    fun saveBitmap(bitmap: Bitmap): Uri{
        var fos : FileOutputStream? = null
        val fileName = generateFileName()
        try{
            fos = Injector.context.openFileOutput(fileName, MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: IOException){
            e.printStackTrace()
        }
        finally {
            try {
                fos?.close()
            }
            catch (exception: Exception){
                exception.printStackTrace()
            }
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