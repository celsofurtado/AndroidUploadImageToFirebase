package br.com.empresa.uploadimagetofirebase

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    lateinit var buttonChoose: Button
    lateinit var buttonUpload: Button
    lateinit var ivImage: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var edNome: EditText
    lateinit var edUri: EditText
    lateinit var filePath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonChoose = findViewById(R.id.button_choose)
        buttonUpload = findViewById(R.id.button_upload)
        ivImage = findViewById(R.id.iv_imagem)
        edNome = findViewById(R.id.ed_nome)
        edUri = findViewById(R.id.ed_uri)

        buttonChoose.setOnClickListener {
            startFileChooser()
        }

        buttonUpload.setOnClickListener {
            uploadFile()
        }

    }

    private fun uploadFile() {

        var t: String = ""

        if (filePath != null) {

            progressBar = findViewById(R.id.progressbar)
            progressBar.visibility = View.VISIBLE

            var imageRef = FirebaseStorage
                            .getInstance()
                            .reference
                            .child("images/${edNome.text.toString()}")

            imageRef.putFile(filePath)
                    .addOnSuccessListener { p0 ->
                        //pd.dismiss()
                        Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.INVISIBLE

                        imageRef
                                .downloadUrl
                                .addOnSuccessListener { uri ->
                                    edUri.setText(uri.toString())
                                }
                    }
                    .addOnFailureListener { p0 ->
                        Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                    }
                    .addOnProgressListener { p0 ->
                        var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                        //pd.setMessage("Uploaded ${progress.toInt()}%")
                    }
        }

        Log.d("Teste", "Uri: $t")
    }

    private fun startFileChooser() {
        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("MainActivity", resultCode.toString())

        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data!!
            //var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            //ivImage.setImageBitmap(bitmap)
            ivImage.setImageURI(filePath)
        }

    }
}