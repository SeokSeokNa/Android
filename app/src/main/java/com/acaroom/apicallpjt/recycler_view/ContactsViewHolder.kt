package com.acaroom.apicallpjt.recycler_view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.data_domain.MemberDto
import kotlinx.android.synthetic.main.item_contacts.view.*

class ContactsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var view : View = v // 화면 받아오기

    //데이터를 그려놓은 화면에 바인드 시키는것
    //받아온 화면에 mName 과 mTel에 각각 Contacts.kt 에 있는 필드들로 연결시키는것
    fun bind(item: MemberDto) {
        view.mUserId.text = item.userId
        view.mUserName.text = item.userName
    }
}