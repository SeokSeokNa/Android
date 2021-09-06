package com.acaroom.apicallpjt.activity

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.dialog.ProgressDialog
import com.acaroom.apicallpjt.recycler_view.GalleryAdapter
import kotlinx.android.synthetic.main.activity_gallery.*
import java.util.*
import kotlin.collections.ArrayList

class GalleryActivity  : AppCompatActivity() {
    var itemList:ArrayList<Uri> = arrayListOf()
    var clickList:ArrayList<Uri> = arrayListOf()
    val adapter = GalleryAdapter(itemList,clickList)

    lateinit var customProgressDialog: ProgressDialog
    lateinit var dialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(this)
        //로딩창 투명하게
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        mGalleryRecycler.adapter = adapter

        val gridLayoutManager = GridLayoutManager(this,3 , GridLayoutManager.VERTICAL , false) //spanCount는 열수
        mGalleryRecycler.layoutManager = gridLayoutManager
//        mRecyclerView.setHasFixedSize(true)

        var hor = DividerItemDecoration(mGalleryRecycler.context, DividerItemDecoration.HORIZONTAL) //세로줄
        var ver = DividerItemDecoration(mGalleryRecycler.context, DividerItemDecoration.VERTICAL) // 가로줄

        mGalleryRecycler.addItemDecoration(hor)
        mGalleryRecycler.addItemDecoration(ver)

        openImagePicker()

        picker_btn.setOnClickListener {
            customProgressDialog.show()
            Log.i("result = " , clickList.toString())
            var intent = Intent()
            intent.putExtra("result_pic",clickList)
            setResult(Activity.RESULT_OK,intent)
            finish()
            customProgressDialog.dismiss()
        }

        cancel_btn.setOnClickListener {
            var intent = Intent()
            intent.putExtra("result_pic",clickList)
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
            customProgressDialog.dismiss()
        }
    }


    private fun openImagePicker() {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor = applicationContext.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = Date(cursor.getLong(dateTakenColumn))
                val displayName = cursor.getString(displayNameColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                Log.i("결과", "id: $id, display_name: $displayName, date_taken: " +
                        "$dateTaken, content_uri: $contentUri"
                )
                itemList.add(contentUri)
                adapter.notifyDataSetChanged()
            }

        }
    }


}