package com.acaroom.apicallpjt.recycler_view

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.gallery_photo_item.view.*


class GalleryAdapter(
    val itemList: ArrayList<Uri>,
    val clickList: ArrayList<Uri>,
    val boardList: ArrayList<Uri>
) : RecyclerView.Adapter<GalleryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
        (parent.context as Activity).pic_count.text = "${boardList.size+clickList.size}"+"/10"
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_photo_item, parent, false)//화면을 가져옴
        return GalleryHolder(inflatedView)
    }



    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        var context = holder.view
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
        var display = context.my_img.resources.displayMetrics
//        context.my_img.scaleType = ImageView.ScaleType.CENTER_CROP
//        context.my_img.layoutParams = LinearLayout.LayoutParams(display.widthPixels/3,display.widthPixels/3)

        holder.view.my_img.setOnClickListener {
            context.my_check.performClick()
        }

        holder.view.my_check.setOnClickListener {
            clickEvent(holder, context, item)

        }
    }
    //리사이클러뷰의 특성인 재사용성 때문에 사진 체크후 스크롤 내리면 해당포지션에 다음 줄 아이템이 체크되있는 문제때문에
    //이걸 써서 막앗음
    override fun getItemViewType(position: Int): Int {
        return position
    }

    //내가 만든것
    private fun clickEvent(
        holder: GalleryHolder,
        context: View,
        item: Uri,

    ) {
        ContextCompat.getDrawable(holder.view.context, R.drawable.ic_check_box_none)
        val check = ContextCompat.getDrawable(holder.view.context, R.drawable.ic_check)
        val none = ContextCompat.getDrawable(holder.view.context, R.drawable.ic_check_box_none)
        if (context.my_check.isChecked) {
            if (boardList.size+clickList.size == 10) return //10개 까지만 선택 가능하도록 변경

            context.my_check.background = check
            clickList.add(item)
        } else {
            context.my_check.background = none
            clickList.remove(item)
        }
        Log.i("크기", "${boardList.size}")


        (context.context as Activity).pic_count.text = "${boardList.size+clickList.size}"+"/10"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}