package com.acaroom.apicallpjt.recycler_view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.data_domain.BoardDto
import com.acaroom.apicallpjt.fragment.board.BoardDetailFragment
import com.acaroom.apicallpjt.fragment.board.BoardFragment
import kotlinx.android.synthetic.main.footer.view.*

class BoardListAdapter(val boardDtoList: ArrayList<BoardDto?>, val boardFragment: BoardFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2
    lateinit var footer_view:View;

    override fun getItemCount(): Int {
        return boardDtoList.size+1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FOOTER ->FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.footer,parent,false))
            else ->  BoardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.board_holder,parent,false))
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is BoardViewHolder) {
            val item = boardDtoList[position]
            (holder as BoardViewHolder).apply {
                if (item != null) {
                    bind(item)
                }
            }
            holder.itemView.setOnClickListener {
                val boardDetailFragment = BoardDetailFragment()
                val bundle = Bundle(1)
                bundle.putInt("id", Integer.parseInt(item?.id))
                bundle.putString("userId", item?.userId)
                boardDetailFragment.arguments = bundle
                (holder.view.context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) //프레그먼트 열릴때 애니메이션 효과
                    .replace(R.id.fragment, boardDetailFragment).addToBackStack("boardDetail")
                    .commit()

                //Toast.makeText(holder.view.context , "${item.id}" , Toast.LENGTH_SHORT).show()
            }
        } else if (holder is FooterViewHolder) {
            Log.e("holder" , "호출!!")
            footer_view = holder.view.footer_btn
            (holder as FooterViewHolder).apply {
                when (boardFragment.hasNext) {
                    true -> holder.view.footer_btn.visibility = View.VISIBLE
                    else -> holder.view.footer_btn.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                boardFragment.initScrollListener()
            }
        }


    }

    fun getFooter(): View {
        return footer_view
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount-1 -> TYPE_FOOTER
            else -> TYPE_ITEM
        }


    }


}