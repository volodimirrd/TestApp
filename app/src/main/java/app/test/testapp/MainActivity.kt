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




class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val MY_STORAGE_WRITE_PERMISSION_CODE = 101
    private var directory : File? = null
    private var currentFilePathUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
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
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentFilePathUri)
            if(bitmap!=null){
             bitmap as Bitmap
                setNewImage(bitmap)
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
}
