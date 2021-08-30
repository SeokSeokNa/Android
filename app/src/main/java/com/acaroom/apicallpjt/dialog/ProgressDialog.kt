package com.acaroom.apicallpjt.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.fragment.board.WriteFormFragment

class ProgressDialog(context: Context) : Dialog(context) {

    init {
        //다이얼 로그 제목을 안보이게 하기
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)
    }
}