package com.example.trackersales

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.trackersales.databinding.FragmentDetailLaporanBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class DetailLaporan : Fragment() {

    private var _binding: FragmentDetailLaporanBinding? = null

    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var sales_id: String? = null
    private var tanggal: String? = null
    private var judul: String? = null
    private var catatan: String? = null
    private var uniqueid: String? = null
    private var long: Double? = null
    private var lat: Double? = null
    var isImageFitToScreen:Boolean?=null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap
        if(long!=null){
                val latLng = lat?.let { long?.let { it1 ->
                    LatLng(it,
                        it1
                    )
                } }
                val markerOptions = latLng?.let { MarkerOptions().position(it) }
                mMap.addMarker(markerOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sales_id = it.getString("sales_id")
            tanggal = it.getString("tanggal")
            judul = it.getString("judul")
            catatan=it.getString("catatan")
            long=it.getDouble("long")
            lat=it.getDouble("lat")
            uniqueid=it.getString("uniqueid")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailLaporanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnDownload.visibility=View.GONE
        val storageRef =  FirebaseStorage.getInstance().reference.child("check-in/"+judul+"/"+uniqueid)
        val localfile = File.createTempFile("tempImage","jpg")
        Log.d("hal",storageRef.toString())
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.imageViewCheckin.setImageBitmap(bitmap)
            binding.btnDownload.setOnClickListener {
                mSaveMediaToStorage(bitmap)
            }
            binding.btnDownload.visibility=View.VISIBLE
        }.addOnFailureListener{

            Toast.makeText(this.context,it.toString()   ,Toast.LENGTH_SHORT).show()
        }

        binding.imageViewCheckin.setOnClickListener(View.OnClickListener {
            if (isImageFitToScreen == true) {
                isImageFitToScreen = false
                binding.imageViewCheckin.setLayoutParams(
                    LinearLayout.LayoutParams(
                        300,
                        300
                    )
                )
                binding.imageViewCheckin.setAdjustViewBounds(true)
            } else {
                isImageFitToScreen = true
                binding.imageViewCheckin.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                )
                binding.imageViewCheckin.setScaleType(ImageView.ScaleType.FIT_XY)
            }
        })




        binding.tvJudul.text=judul
        binding.editTextTextMultiLine.setText(catatan)
        binding.tanggalLaporan.text=tanggal
        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapLaporan) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }
    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.context?.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this.context , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }
}