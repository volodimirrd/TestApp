package app.test.testapp.storage

import android.net.Uri
import android.provider.Settings
import app.test.testapp.TestAppApplication
import app.test.testapp.UI.UploadingListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImageStorage{

    companion object {
         val instance: ImageStorage = ImageStorage()
    }

    private var uniqueId: String = Settings.Secure.getString(TestAppApplication.instance.getContentResolver(), Settings.Secure.ANDROID_ID)

    fun uploadImage(currentFilePathUri : Uri?, listener: UploadingListener) {
        if (currentFilePathUri != null) {
            listener.onStartUploading()

            val ref = FirebaseStorage.getInstance().reference.child("images/$uniqueId")
            ref.putFile(currentFilePathUri)
                .addOnSuccessListener {
                    listener.onSuccess()
                }
                .addOnFailureListener { e ->
                    listener.onError()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    listener.setCurrentProgressing(progress.toInt())
                }
        }
    }

    fun getImageReference(): StorageReference? {
        return FirebaseStorage.getInstance().reference.child("images/$uniqueId")
    }
}