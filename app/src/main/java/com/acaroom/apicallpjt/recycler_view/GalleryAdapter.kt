package com.acaroom.apicallpjt.recycler_view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import kotlinx.android.synthetic.main.gallery_item.view.*

class GalleryAdapter(val itemList: ArrayList<Uri>, val clickList:ArrayList<Uri>) : RecyclerView.Adapter<GalleryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_item, parent, false)//화면을 가져옴
        return GalleryHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        var context = holder.view

        val item = itemList[position]
        holder.apply {
            bind(item)
        }

        holder.view.my_img.setOnClickListener {
            context.my_check.performClick()
        }

        holder.view.my_check.setOnClickListener {
            clickEvent(holder, context, item)

        }
    }

    //내가 만든것
    private fun clickEvent(
        holder: GalleryHolder,
        context: View,
        item: Uri
    ) {
        ContextCompat.getDrawable(holder.view.context, R.drawable.ic_check_box_none)
        val check = ContextCompat.getDrawable(holder.view.context, R.drawable.ic_check)
        val none = ContextCompat.getDrawable(holder.view.context, R.drawable.ic_check_box_none)
        if (context.my_check.isChecked) {
            context.my_check.background = check
            clickList.add(item)
        } else {
            context.my_check.background = none
            clickList.remove(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}