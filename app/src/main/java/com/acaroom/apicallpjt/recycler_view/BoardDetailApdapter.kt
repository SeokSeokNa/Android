package com.acaroom.apicallpjt.recycler_view

import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.data_domain.PhotoDto
import kotlinx.android.synthetic.main.detail_photo_holder.view.*

class BoardDetailApdapter(val photo_list:ArrayList<PhotoDto>) : RecyclerView.Adapter<BoardDetailHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardDetailHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.detail_photo_holder , parent , false)
        return BoardDetailHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BoardDetailHolder, position: Int) {
        val drawable = ContextCompat.getDrawable(holder.view.context, R.drawable.image_rec) as GradientDrawable
        //각 사진별로 테두리 동그랗게 하는건 xml만으로 해결되지 않아서 이렇게 또 설정해야함
        holder.view.detail_photo.background = drawable
        holder.view.detail_photo.clipToOutline = true


        var item = photo_list[position]
        holder.apply {
            bind(item)
        }
    }

    override fun getItemCount(): Int {
        return photo_list.size
    }


}