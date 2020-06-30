package km.project.firebaseapplication

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import km.project.firebaseapplication.Util.rotate
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var storageReference: StorageReference
    private lateinit var filePath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storageReference = FirebaseStorage.getInstance().reference

        btn_select.setOnClickListener {
            showGallery()
        }

        btn_delete.setOnClickListener {
            img_select.setImageBitmap(null)
        }

        btn_delete.setOnLongClickListener {
            fileDelete()
            return@setOnLongClickListener true
        }

        btn_upload.setOnClickListener {
            uploadImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            filePath = data.data!!

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                img_select.setImageBitmap(bitmap.rotate(DEGREES))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showGallery() {
        startActivityForResult(Intent.createChooser(Intent().apply {
            type = TYPE
            action = Intent.ACTION_GET_CONTENT
        }, getString(R.string.str_gallery_select)), REQUEST_CODE)
    }

    private fun uploadImage() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(R.string.image_upload_title)
        progressDialog.show()

        filePath.let {
            storageReference.child(IMAGE_PATH).run {
                putFile(filePath)
            }.addOnSuccessListener { _ ->
                progressDialog.dismiss()
                Util.toast(this, getString(R.string.upload_success))
            }
                .addOnFailureListener { exception ->
                    progressDialog.dismiss()
                    Util.toast(this, exception.message)
                }
                .addOnProgressListener { snapShot ->
                    val loadingMessage = String.format(
                        Locale.getDefault(),
                        "%s",
                        (PERSENT * snapShot.bytesTransferred / snapShot.totalByteCount).toInt()
                            .toString() + "%"
                    )
                    progressDialog.setMessage(loadingMessage)
                }
        }
    }

    private fun fileDelete() {
        storageReference.child(IMAGE_PATH).apply {
            delete().addOnSuccessListener {
                Util.toast(this@MainActivity, "Delete Success")
            }.addOnFailureListener { exception ->
                Util.toast(this@MainActivity, exception.toString())
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1000
        private const val IMAGE_PATH = "images"
        private const val TYPE = "image/*"
        private const val DEGREES = 90F
        private const val PERSENT = 100.0
    }
}

