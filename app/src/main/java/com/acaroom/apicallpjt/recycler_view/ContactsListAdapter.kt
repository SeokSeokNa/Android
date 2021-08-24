package com.acaroom.apicallpjt.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.data_domain.MemberDto

/*정확히 어떤 화면에 연결시킬건지 설정하는 역활
* 데이터를 상세 컨트롤 하는 역활
* */
class ContactsListAdapter(val itemList : ArrayList<MemberDto>) : RecyclerView.Adapter<ContactsViewHolder>() {
    //생성자로 부터 받은 데이터의 갯수를 측정해야 합니다
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacts, parent, false)//화면을 가져옴
        return ContactsViewHolder(inflatedView) //ViewHolder에 가져온 화면을 넘겨줌(ContactsViewHolder 생성!!)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item) //화면에 값 셋팅하기(ContactsViewHolder.kt에 있는 함수 호출)
        }
    }
}