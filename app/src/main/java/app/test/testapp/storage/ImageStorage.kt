package app.test.testapp.storage

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.Settings
import app.test.testapp.Injector
import app.test.testapp.UI.UploadingListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImageStorage{

    companion object {
         val instance: ImageStorage = ImageStorage()
    }

    @SuppressLint("HardwareIds")
    private var uniqueId: String = Settings.Secure.getString(Injector.context.contentResolver, Settings.Secure.ANDROID_ID)

    fun uploadImage(currentFilePathUri : Uri?, listener: UploadingListener) {
        if (currentFilePathUri != null) {
            listener.onStartUploading()

            val ref = getImageReference()
            ref?.putFile(currentFilePathUri)
                ?.addOnSuccessListener {
                    listener.onSuccess()
                }
                ?.addOnFailureListener { e ->
                    listener.onError(e)
                }
                ?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    listener.setCurrentProgressing(progress.toInt())
                }
        }
    }

    fun getImageReference(): StorageReference? {
        return FirebaseStorage.getInstance().reference.child("images/$uniqueId")
    }
}