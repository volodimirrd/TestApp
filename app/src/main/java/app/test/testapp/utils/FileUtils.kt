package app.test.testapp.utils

import android.os.Environment
import java.io.File
import android.net.Uri


object FileUtils {
    fun createDirectory():File{
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyFolder")
        if(!directory.exists()){
            directory.mkdirs()
        }
        return directory
    }

    fun generateFileUri(directory:File): Uri {
         var file: File? = null
         file = File(directory.path + "/" + "photo_" + System.currentTimeMillis() + ".jpg")
         return Uri.fromFile(file)
     }
}