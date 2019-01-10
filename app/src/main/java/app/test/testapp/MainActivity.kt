package app.test.testapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.Toast
import app.test.testapp.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import android.os.StrictMode
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.OnProgressListener
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.UUID.randomUUID
import android.app.ProgressDialog
import com.bumptech.glide.Glide
import java.util.*


class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val MY_STORAGE_WRITE_PERMISSION_CODE = 101
    private var directory : File? = null
    private var currentFilePathUri : Uri? = null

    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        storage = FirebaseStorage.getInstance();
        storageReference = storage?.getReference();
        setSupportActionBar(toolbar)
    }

    private fun initView(){
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if(directory == null){
            createDirectory()
        }
        takePhotoFab.setOnClickListener { view -> onTakePhotoClick(view) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            try{
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentFilePathUri)
                if(bitmap!=null){
                    bitmap as Bitmap
                    setNewImage(bitmap)
             //       uploadFiles()
                }
            }
            catch (e: IOException){
                e.printStackTrace()
            }

        }
    }

    private fun uploadFiles(){

        Glide.with(this)
            .load(storageReference)
            .into(photoImageView)
    }

    private fun onTakePhotoClick(view: View){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    MY_CAMERA_PERMISSION_CODE)
            } else{
                starCamera()
            }
        } else{
            starCamera()
        }
    }

    private fun createDirectory(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_STORAGE_WRITE_PERMISSION_CODE)
            } else{
                directory = FileUtils.createDirectory()
            }
        }
        else {
            directory = FileUtils.createDirectory()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_CAMERA_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                starCamera()
            } else{
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == MY_STORAGE_WRITE_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                directory = FileUtils.createDirectory()
            } else{
                Toast.makeText(this, "write storage permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun starCamera(){
        if(directory != null){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            currentFilePathUri = FileUtils.generateFileUri(directory!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentFilePathUri)
            startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    private fun setNewImage(bitmap:Bitmap){
        photoImageView.setImageBitmap(bitmap)
    }

    private fun uploadImage() {

        if (currentFilePathUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref = storageReference?.child("images/" + UUID.randomUUID().toString())
            ref?.putFile(currentFilePathUri!!)
                ?.addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
                ?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                }
        }
    }
}
