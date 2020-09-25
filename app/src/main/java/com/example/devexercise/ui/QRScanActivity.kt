package com.example.devexercise.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.devexercise.R
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_qr_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

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
    }

    override fun handleResult(rawResult: Result?) {
        val data = Intent()
        data.putExtra("textResult", rawResult!!.text)
        setResult(Activity.RESULT_OK, data)
        finish()
    }


}
