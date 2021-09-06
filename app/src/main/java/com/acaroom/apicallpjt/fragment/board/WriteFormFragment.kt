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
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.App
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.activity.GalleryActivity
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.dialog.ProgressDialog
import com.acaroom.apicallpjt.recycler_view.BoardFormAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.board_holder.view.*
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
    var images = ArrayList<MultipartBody.Part>()
    var destFiles: ArrayList<File>? = arrayListOf()

    lateinit var customProgressDialog: ProgressDialog
    lateinit var dialog: AlertDialog.Builder
    var handler = Handler()
    var boardPhotoList : ArrayList<Uri> = arrayListOf()
    val adapter = BoardFormAdapter(boardPhotoList)
    var retrofit = Config.getApiClient()


    var boardService = retrofit.create(BoardService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write_form, container, false)

        //제목 입력후 엔터키 누르면 내용 입력칸으로 이동
        view.board_title.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
               view.board_content.requestFocus()
            }
            true
        }


        dialog = AlertDialog.Builder(view.context)
        dialog.setTitle("알림")

        var activity: MainActivity
        activity = (getActivity() as MainActivity?)!!
        var recyclerView = view.upload_photo_list;

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        //아이템별 경계선 그리기
        var div_deco = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        recyclerView.addItemDecoration(div_deco)


        view.my_image_btn.setOnClickListener {
            if (checkPermission()) { //권한받았으면 이미지피커 갤러리 실행
                //openGalleryForImage()
                openImagePicker()
            } else { //권한 안받았으면 권한 요청 메세지 실행
                requestPermission()
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

                //로딩화면 일부러 보여주려고 2초 지연줫음 ...
                customProgressDialog.show()
                postBoard(title, content)
//                handler.postDelayed({
//
//                }, 500)

            }


        }

        return view
    }


    private fun postBoard(title: String, content: String) {
        if (destFiles?.size !=0) {

            for (file in destFiles!!) {
                // RequestBody로 변환
                var requestBmp = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                // MultipartBody.Part로 파일 컨버전
                var bmp = MultipartBody.Part.createFormData("files", file.name, requestBmp)
                images.add(bmp)
            }

        }


        val title_result = RequestBody.create(okhttp3.MultipartBody.FORM, title)
        val content_result = RequestBody.create(okhttp3.MultipartBody.FORM, content)




        var map  = HashMap<String, RequestBody>()

        map.put("title", title_result)
        map.put("content", content_result)

        boardService.postBoard2(map, images)
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
                                    var activity = (getActivity() as MainActivity?)!!
                                    activity.supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragment, BoardFragment())
                                        .commit()
                                })

                            dialog.setMessage("등록되었습니다!").show()

                        }
                    }


                }

                override fun onFailure(call: Call<Number>, t: Throwable) {
                    Log.i("결과 메세지", t.message.toString())
                    Log.i("결과 stackTrace", t.stackTrace.toString())
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

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_TAKE)

    }

    //이미지피커 갤러리 열기(내가 만든거임)
    private fun openImagePicker() {
        var intent = Intent(context,GalleryActivity::class.java)
        startActivityForResult(intent,REQUEST_GALLERY_TAKE)
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

                    if (data != null) {
                        var result_list: java.util.ArrayList<Uri> = data.getSerializableExtra("result_pic") as java.util.ArrayList<Uri>
                        for (uri in result_list) {
                            Log.i("결과" , uri.toString())
                            boardPhotoList.add(uri)
                            adapter.notifyDataSetChanged()
                            var imagePath = getRealPathFormURI(uri)
                            destFiles?.add(File(imagePath))

                        }
                    }


//                    if (data?.clipData == null) {
//                        Log.i("사진 하나만 !", data?.data?.path.toString())
//                        var uri = data?.data
//                        if (uri != null) {
//                            boardPhotoList.add(uri)
//                            adapter.notifyDataSetChanged()
//                            var imagePath = getRealPathFromURI2(uri)
//                            if (imagePath != null) {
//                                Log.i("uri결과 !", imagePath)
//                            }
//                            destFiles?.add(File(imagePath))
//                        }
//
//                    } else {
//                        Log.i("사진 여러개 !", data.clipData!!.getItemAt(0).uri.path.toString())
//                        var clipDataResult = data.clipData
//                        if (clipDataResult != null) {
//                            for (i in 0 until clipDataResult.itemCount) {
//                                var uri = clipDataResult.getItemAt(i).uri
//                                boardPhotoList.add(uri)
//                                adapter.notifyDataSetChanged()
//                                var imagePath = getRealPathFromURI2(uri)
//                                destFiles?.add(File(imagePath))
//                            }
//
//
//                        }
//
//                    }

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

    //이미지 파일 절대경로 가져오기(여러 이미지를 파일형태로 가져오기 위함)
    private fun getRealPathFromURI2(contentUri: Uri): String? {
        if (contentUri.path!!.startsWith("/storage")) {
            return contentUri.path
        }
        val id = DocumentsContract.getDocumentId(contentUri).split(":".toRegex()).toTypedArray()[1]
        val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = MediaStore.Files.FileColumns._ID + " = " + id
        val cursor = activity?.contentResolver?.query(
            MediaStore.Files.getContentUri("external"),
            columns,
            selection,
            null,
            null
        )
        try {
            val columnIndex: Int = cursor!!.getColumnIndex(columns[0])
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor!!.close()
        }
        return null
    }


    //카메라 권한 요청 (카메라 접근 및 저장소 접근에 대한 요청)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity as MainActivity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            REQUEST_IMAGE_CAPTURE
        )
    }

    //카메라 권한 체크( 카메라 접근 및 저장소 접근에 대한 체크)
    private fun checkPermission(): Boolean {
        return (
                ContextCompat.checkSelfPermission(activity as MainActivity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&  //카메라 권한
                ContextCompat.checkSelfPermission(activity as MainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && //저장소 읽기
                ContextCompat.checkSelfPermission(activity as MainActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED //저장소 쓰기
                )
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