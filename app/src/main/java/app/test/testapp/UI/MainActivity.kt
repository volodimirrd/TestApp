package app.test.testapp.UI

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
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
import java.io.IOException
import app.test.testapp.R
import app.test.testapp.storage.GlideApp
import app.test.testapp.storage.ImageStorage
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MainActivity : AppCompatActivity(), UploadingListener {


    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val MY_STORAGE_WRITE_PERMISSION_CODE = 101
    private var directory : File? = null
    private var currentFilePathUri : Uri? = null
    private lateinit var repository: ImageStorage
    private var progressDialog : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        initView()
    }

    private fun initView(){
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if(directory == null){
            createDirectory()
        }
        progressDialog = ProgressDialog(this)

        repository = ImageStorage(this)
        loadImage()
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
                    repository.uploadImage(currentFilePathUri, this)
                }
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
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

    private fun loadImage(){
        val ref = repository.getImageReference()

        GlideApp.with(this)
            .load(ref)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(photoImageView)
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog(title:String) {
        progressDialog?.setTitle(title)
        progressDialog?.show()
    }

    override fun closeProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun setCurrentProgressing(progress:Int) {
        progressDialog?.setMessage("Uploaded $progress%")
    }
}
