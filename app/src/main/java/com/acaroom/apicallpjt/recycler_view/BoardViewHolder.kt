package com.acaroom.apicallpjt.recycler_view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.board_holder.view.*

class BoardViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var view : View = v // 화면 받아오기

    //데이터를 그려놓은 화면에 바인드 시키는것
    fun bind(boardDto: BoardDto) {
        view.mTitle.text = boardDto.title
        view.mContents.text = boardDto.contents
        view.mWriteDate.text = boardDto.writeDate
        view.userName.text = boardDto.userName
        Picasso.get().load(Config.image+"${boardDto.photoList.get(0).fileName}")
            .resize(100,100)
            .into(view.imageView)
    }


}