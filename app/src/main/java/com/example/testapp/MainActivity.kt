package com.example.testapp

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.InterstitialAd

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity(), ImageInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView
    private lateinit var adapter: ImagesAdapter
    private lateinit var imageList: ArrayList<Int>
    private var fbRemoteConfig: FirebaseRemoteConfig? = null
    var REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fbRemoteConfig = FirebaseRemoteConfig.getInstance()
        fbRemoteConfig!!.setDefaultsAsync(R.xml.remote_config)

        val testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(
            this
        )

        fbRemoteConfig!!.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().build())

        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.taskHeader)

        fbRemoteConfig!!.activate().addOnSuccessListener {
//            Toast.makeText(applicationContext, "True", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Log.d("Error", "$it")
        }
        applyConfig()
        fbRemoteConfig!!.fetch(0);


        recyclerView.layoutManager = GridLayoutManager(this, 2)
        imageList = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {
            imageList.add(R.drawable.first)
            imageList.add(R.drawable.second)
            imageList.add(R.drawable.third)
            imageList.add(R.drawable.fourth)
            imageList.add(R.drawable.fifth)
            imageList.add(R.drawable.sixth)
            imageList.add(R.drawable.seventh)
            imageList.add(R.drawable.eighth)
            imageList.add(R.drawable.ninth)
            imageList.add(R.drawable.tenth)

            adapter = ImagesAdapter(imageList, this@MainActivity)
            val admobNativeAdAdapter = AdmobNativeAdAdapter.Builder
                .with(
                    "ca-app-pub-3940256099942544/2247696110",  //Create a native ad id from admob console
                    adapter,  //The adapter you would normally set to your recyClerView
                    "custome" //Set it with "small","medium" or "custom"
                )
                .adItemIterval(3) //native ad repeating interval in the recyclerview
                .build()


            CoroutineScope(Dispatchers.Main).launch {
                recyclerView.adapter = admobNativeAdAdapter
            }
        }

    }

    private fun applyConfig() {
        textView.text = fbRemoteConfig!!.getString("header_text")
    }

    override fun DownloadImage(current: Int, position: Int) {
        Log.d("TAG", "${position / 2}")
        if (position % 2 == 0) {
            var mInterstitialAd = InterstitialAd(this)

            mInterstitialAd!!.adUnitId = "ca-app-pub-3940256099942544/1033173712"
            // Creating  a Ad Request
            val adRequest = AdRequest.Builder().build()

            // load Ad with the Request
            mInterstitialAd.loadAd(adRequest)
            mInterstitialAd.setAdListener(object : AdListener(){
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mInterstitialAd.show()
                }
            })

        }

            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                saveImage(current)
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE
                )
            }

    }

    private fun saveImage(current: Int) {
        var uriImage: Uri = Uri.EMPTY
        var contentResolver = contentResolver

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
            uriImage = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }
        else{
            uriImage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        var contentValue = ContentValues()
        contentValue.put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
        contentValue.put(MediaStore.Images.Media.MIME_TYPE,"images/*")
        var uri: Uri = contentResolver.insert(uriImage,contentValue) as Uri

        try {
            var bitmap = BitmapFactory.decodeResource(resources,current)
            var outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri))
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
            Objects.requireNonNull(outputStream)

            Toast.makeText(applicationContext, "Image Saved!", Toast.LENGTH_LONG).show()
        }
        catch (exception: Exception){
            Toast.makeText(applicationContext, "Error Occured", Toast.LENGTH_LONG).show()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Permission Granted",  Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(applicationContext, "Permission required to save the photo!", Toast.LENGTH_LONG).show()
            }
        }
    }
}