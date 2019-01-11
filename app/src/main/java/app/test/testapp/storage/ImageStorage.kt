package app.test.testapp.storage

import android.content.Context
import android.net.Uri
import android.provider.Settings
import app.test.testapp.UI.UploadingListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImageStorage(val context: Context) {

    private var uniqueId: String = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null


    init {
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
    }

    fun uploadImage(currentFilePathUri : Uri?, uploadInterface: UploadingListener) {
        if (currentFilePathUri != null) {
            uploadInterface.showProgressDialog("Uploading...")

            val ref = storageReference?.child("images/$uniqueId")
            ref?.putFile(currentFilePathUri)
                ?.addOnSuccessListener {
                    uploadInterface.closeProgressDialog()
                    uploadInterface.showToast("Uploaded")
                }
                ?.addOnFailureListener { e ->
                    uploadInterface.closeProgressDialog()
                    uploadInterface.showToast("Failed ")
                }
                ?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    uploadInterface.setCurrentProgressing(progress.toInt())
                }
        }
    }

    fun getImageReference(): StorageReference? {
        return storageReference?.child("images/$uniqueId")
    }
}