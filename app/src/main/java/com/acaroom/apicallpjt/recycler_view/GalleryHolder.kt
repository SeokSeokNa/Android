package com.acaroom.apicallpjt.recycler_view

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gallery_photo_item.view.*

class GalleryHolder(v: View):RecyclerView.ViewHolder(v) {

    var view:View = v



    fun bind(item: Uri) {
        Picasso.get().load(item)
            //.resize(768,0)
            //.fit()
            .fit()
            .centerCrop()
            //.resize(700,700)
            .error(R.drawable.ic_camera)
            .into(view.my_img)

//        view.my_img.setOnClickListener{
//            view.my_check.performClick()
//        }
//
//
//        view.my_check.setOnClickListener {
//            val check = view.context.resources.getDrawable(R.drawable.ic_check)
//            val none = view.context.resources.getDrawable(R.drawable.ic_check_box_none)
//            if (view.my_check.isChecked) {
//                view.my_check.background = check
//            }  else {
//                view.my_check.background = none
//            }
//
//        }
    }
}