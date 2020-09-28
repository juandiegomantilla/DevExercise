package com.example.devexercise.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.devexercise.R
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_qr_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

const val OPEN_GALLERY_REQUEST = 100

class QRScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    zxscanner.setResultHandler(this@QRScanActivity)
                    zxscanner.startCamera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) { }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@QRScanActivity, "You should enable this permission", Toast.LENGTH_LONG).show()
                }
            }).check()

        gallery_button.setOnClickListener {
            openGalleryForImage()
        }
    }

    override fun handleResult(rawResult: Result?) {
        val data = Intent()
        if(rawResult != null){
            data.putExtra("textResult", rawResult.text)
        }else{
            data.putExtra("textResult", "QR Code not detected")
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY_REQUEST)
    }

    private fun scanQRFromGallery(imageUri: Uri){
        val bMap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

        val intArray = IntArray(bMap.width * bMap.height)
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader: Reader = MultiFormatReader()
        try {
            val result: Result = reader.decode(bitmap)
            handleResult(result)
        } catch (e: Exception) {
            CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_GALLERY_REQUEST){
            val imageUri: Uri = data?.data!!
            scanQRFromGallery(imageUri)
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
            if (result != null) {
                scanQRFromGallery(result.uri)
            }else{
                handleResult(null)
            }
        }
    }
}
