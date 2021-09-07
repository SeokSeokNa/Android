package com.acaroom.apicallpjt.recycler_view

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.data_domain.PhotoDto
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_photo_holder.view.*

class BoardDetailHolder(v:View) : RecyclerView.ViewHolder(v) {
    var view = v

    fun bind(photo: PhotoDto) {
        Log.i("파일이름","${photo.fileName}")
            Picasso.get().load(Config.image + "${photo.fileName}")
                .fit()
               // .resize(500,500)
                .centerCrop()
                .into(view.detail_photo)


    }
}