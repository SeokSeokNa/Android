package com.acaroom.apicallpjt.recycler_view

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.board_holder.view.*
import kotlinx.android.synthetic.main.board_photo_item_holder.view.*
import java.io.File

class BoardFormPhotoHolder(v:View)  : RecyclerView.ViewHolder(v) {

    var view : View = v

    fun bind(uri: Uri) {
        Picasso.get().load(uri)
            .resize(500,500)
            .into(view.upload_photo)
        //view.upload_photo.setImageURI(uri)
    }
}