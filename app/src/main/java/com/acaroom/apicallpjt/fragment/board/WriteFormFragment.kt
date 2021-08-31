package com.acaroom.apicallpjt.fragment.board

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.acaroom.apicallpjt.App
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.dialog.ProgressDialog
import kotlinx.android.synthetic.main.fragment_write_form.*
import kotlinx.android.synthetic.main.fragment_write_form.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class WriteFormFragment : Fragment() {
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLERY_TAKE = 2
    lateinit var destFile:File
    lateinit var customProgressDialog: ProgressDialog
    lateinit var dialog: AlertDialog.Builder
    var handler = Handler()
    var retrofit = Retrofit.Builder()
        .baseUrl(Config.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    var boardService = retrofit.create(BoardService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write_form, container, false)

        dialog = AlertDialog.Builder(view.context)
        dialog.setTitle("알림")

        var activity: MainActivity
        activity = (getActivity() as MainActivity?)!!


        view.my_image.setOnClickListener {
            if (checkPermission()) { //권한받았으면 카메라 실행
                openGalleryForImage()
            } else { //권한 안받았으면 권한 요청 메세지 실행
                requestPermission()
            }

        }


        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {

                }
            }

        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(view.context)
        //로딩창 투명하게
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        view.regist_btn.setOnClickListener {
            var title = view.board_title.text.toString()
            var content = view.board_content.text.toString()



            if (title == "") {
                Toast.makeText(view.context, "제목을 입력하세요", Toast.LENGTH_SHORT).show()
            } else if (content == "") {
                Toast.makeText(view.context, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
            } else {

                customProgressDialog.show()
                //로딩화면 일부러 보여주려고 2초 지연줫음 ...
                handler.postDelayed({
                    postBoard(title, content)
                }, 2000)

            }


        }

        return view
    }


    private fun postBoard(title: String, content: String) {
        // RequestBody로 변환
        var requestBmp = RequestBody.create(MediaType.parse("multipart/form-data"), destFile)
        // MultipartBody.Part로 파일 컨버전
        var bmp = MultipartBody.Part.createFormData("files", destFile.name, requestBmp)

        val title_result = RequestBody.create(okhttp3.MultipartBody.FORM, title)
        val content_result = RequestBody.create(okhttp3.MultipartBody.FORM, content)

        var images = ArrayList<MultipartBody.Part>()
        images.add(bmp)


        var map  = HashMap<String, RequestBody>()

        map.put("title", title_result)
        map.put("content", content_result)

        boardService.postBoard2(App.prefs.token, map, images)
            .enqueue(object : Callback<Number> {
                override fun onResponse(call: Call<Number>, response: Response<Number>) {


                    if (response.isSuccessful) {
                        var result = response.body()
                        if (result != null) {
                            Log.i("결과 : ", "${result} ")
                            dialog.setPositiveButton(
                                "예",
                                DialogInterface.OnClickListener { _, _ ->
                                    customProgressDialog.dismiss()

                                })

                            dialog.setMessage("등록되었습니다!").show()

                        }
                    }


                }

                override fun onFailure(call: Call<Number>, t: Throwable) {
                    Log.i("결과 메세지" , t.message.toString())
                    Log.i("결과 stackTrace" , t.stackTrace.toString())
                    Log.i("실패 : ", "전송 실패!!!")
                    customProgressDialog.dismiss()
                    dialog.setPositiveButton("예", DialogInterface.OnClickListener { _, _ ->
                        customProgressDialog.dismiss()
                    })
                    dialog.setMessage("등록에 실패하였습니다 관리자에게 문의하세요!").show()

                }
            })
    }


    //갤러리 열기
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_TAKE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("코드", requestCode.toString())
        when (requestCode) {
            0 -> {
                Toast.makeText(activity as MainActivity, "사진 선택 취소", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                if (requestCode == REQUEST_GALLERY_TAKE && resultCode == Activity.RESULT_OK) {
                    Log.i("패스", data?.data?.path.toString())
                    var uri = data?.data
                    my_image.setImageURI(uri)
                    var imagePath = getRealPathFormURI(uri)
                    destFile = File(imagePath)
                }
            }
        }
    }


    //이미지 파일 절대경로 가져오기(이미지를 파일형태로 가져오기 위해 절대경로가 필요함)
        private fun getRealPathFormURI(contentURI: Uri?): String {
        var result :String
        var cursor = activity?.contentResolver?.query(contentURI!!, null, null, null, null)
        if (cursor == null) {
            result = contentURI?.path.toString()
        } else {
            cursor.moveToFirst()
            var idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }




//    private fun getRealPathFormURI(@NotNull contentURI: Uri?): String? {
//        if (contentURI?.path?.startsWith("/storage") == true) {
//            return contentURI.path.toString()
//        }
//        var id = DocumentsContract.getDocumentId(context ,contentURI).split(":")[1]
//        var columns= Array<String>(3){MediaStore.Files.FileColumns.DATA}
//
//        var selection = MediaStore.Files.FileColumns._ID + " = " + id;
//        var cursor =
//            activity?.contentResolver?.query(MediaStore.Files.getContentUri("external"),
//                columns, selection, null, null);
//
//        try {
//            var columnIndex = cursor?.getColumnIndex(columns[0])
//            if (cursor?.moveToFirst() == true) {
//                return cursor.getString(columnIndex!!)
//            }
//        } finally {
//            cursor?.close()
//        }
//        return null
//
//    }




    //카메라 권한 요청 (카메라 접근 및 저장소 접근에 대한 요청)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity as MainActivity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            REQUEST_IMAGE_CAPTURE
        )
    }

    //카메라 권한 체크( 카메라 접근 및 저장소 접근에 대한 체크)
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            activity as MainActivity,
            android.Manifest.permission.CAMERA
        ) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity as MainActivity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    //권한요청 결과
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(
                "TAG",
                "Permission: " + permissions[0] + "was" + grantResults[0] + "카메라 허가 받음 예이^^"
            )
        } else {
            Log.d("TAG", "카메라 허가 못받음 ㅠ 젠장!!")
        }
    }


}