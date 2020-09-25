package com.example.devexercise.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.R
import com.example.devexercise.databinding.ActivityLoginBinding
import com.example.devexercise.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


const val REQUEST_CODE = 100
const val RC_BARCODE_CAPTURE = 9001

class LoginActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityLoginBinding

    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.viewModel = viewModel

        binding.inputUsername.addTextChangedListener(loginTextWatcher)
        binding.inputPassword.addTextChangedListener(loginTextWatcher)

        binding.loginButton.setOnClickListener {
            try{
                val username = input_username.text.toString()
                val password = input_password.text.toString()
                val remember = binding.rememberMeSwitch.isChecked

                val status = viewModel.login(username, password, remember)

                status.observe(this, Observer { loginStatus ->
                    when(loginStatus){
                        "Success" -> goToHomeScreen()
                        "Not_Success" -> Snackbar.make(it, "Incorrect Credentials", Snackbar.LENGTH_LONG).show()
                        "Not_Connected" -> Snackbar.make(it, "Not Connected to the Internet", Snackbar.LENGTH_LONG).show()
                    }
                })
            }catch(e: Exception){
                println(e)
            }
        }

        binding.scanQrButton.setOnClickListener {
            openGalleryForImage()
            //val qrScanIntent = Intent(this, QRScanActivity::class.java)
            //startActivityForResult(qrScanIntent, RC_BARCODE_CAPTURE)
        }
    }

    private fun scanQRFromGallery(bMap: Bitmap): String {
        var contents: String? = null

        val intArray = IntArray(bMap.width * bMap.height)
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

        val source: LuminanceSource =
            RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader: Reader = MultiFormatReader()
        try {
            val result: Result = reader.decode(bitmap)
            contents = result.text
        } catch (e: Exception) {
            println("Error decoding QR code: $e")
        }
        return contents!!
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data!!)
            val decoded = scanQRFromGallery(bitmap)
            println("Secret message: $decoded")
            //Snackbar.make(this.login_layout, decoded, Snackbar.LENGTH_LONG).show()
        }
        if(resultCode == Activity.RESULT_OK && requestCode == RC_BARCODE_CAPTURE){
            println("Secret message: ${data?.getStringExtra("textResult")}")
        }
    }

    private fun goToHomeScreen(){
        attempts++
        if(attempts == 1){
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        }
    }

    private val loginTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val userInput = binding.inputUsername.text.toString().trim()
            val passInput = binding.inputPassword.text.toString().trim()

            binding.loginButton.isEnabled = (userInput.isNotEmpty() && passInput.isNotEmpty())
            binding.rememberMeSwitch.isEnabled = (userInput.isNotEmpty() && passInput.isNotEmpty())
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}