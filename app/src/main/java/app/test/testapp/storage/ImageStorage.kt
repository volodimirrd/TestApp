package app.test.testapp.storage

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.load.engine.DiskCacheStrategy
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

    fun uploadImage(currentFilePathUri : Uri?) {
        if (currentFilePathUri != null) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref = storageReference?.child("images/$uniqueId")
            ref?.putFile(currentFilePathUri)
                ?.addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
                ?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                }
        }
    }

    fun downloadImage(imageView: ImageView){
        val islandRef = storageReference?.child("images/$uniqueId")

        GlideApp.with(imageView.context)
            .load(islandRef)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
    }
}