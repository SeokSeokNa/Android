package com.acaroom.apicallpjt.fragment.board

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acaroom.apicallpjt.Config
import com.acaroom.apicallpjt.MainActivity
import com.acaroom.apicallpjt.R
import com.acaroom.apicallpjt.activity.GalleryActivity
import com.acaroom.apicallpjt.apiService.BoardService
import com.acaroom.apicallpjt.data_domain.PhotoDto
import com.acaroom.apicallpjt.dialog.ProgressDialog
import com.acaroom.apicallpjt.recycler_view.BoardFormAdapter
import kotlinx.android.synthetic.main.activit_login.*
import kotlinx.android.synthetic.main.fragment_board_modify_fragment.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BoardModifyFragment : Fragment(), MainActivity.onKeyBackPressedListener {
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLERY_TAKE = 2
    var images = ArrayList<MultipartBody.Part>()
    var destFiles: ArrayList<File>? = arrayListOf()

    lateinit var customProgressDialog: ProgressDialog
    lateinit var dialog: AlertDialog.Builder
    var handler = Handler()
    var boardPhotoList: ArrayList<Uri> = arrayListOf()
    val adapter = BoardFormAdapter(boardPhotoList)
    var retrofit = Config.getApiClient()

    var boardService = retrofit.create(BoardService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_board_modify_fragment, container, false)
        val id = arguments?.getInt("id")!!
        val userId = arguments?.getString("userId")!!
        val title = arguments?.getString("title")
        val content = arguments?.getString("content")
        val photoList = arguments?.getSerializable("photo_list") as ArrayList<PhotoDto>


        view.board_title.setText(title)
        view.board_content.setText(content)

        for (photo in photoList) {
            val uri = Uri.parse(Config.image + photo.fileName)
            boardPhotoList.add(uri)
            adapter.notifyDataSetChanged()
        }



        dialog = AlertDialog.Builder(view.context)
        dialog.setTitle("알림")




        view.my_image_btn.setOnClickListener {
            if (checkPermission()) { //권한받았으면 이미지피커 갤러리 실행
                //openGalleryForImage()
                openImagePicker()
            } else { //권한 안받았으면 권한 요청 메세지 실행
                requestPermission()
            }

        }


        var activity: MainActivity
        activity = (getActivity() as MainActivity?)!!
        var recyclerView = view.upload_photo_list

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        //아이템별 경계선 그리기
        var div_deco = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        recyclerView.addItemDecoration(div_deco)

        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(view.context)
        //로딩창 투명하게
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.modify_btn.setOnClickListener {
            var title = view.board_title.text.toString()
            var content = view.board_content.text.toString()

            if (title == "") {
                Toast.makeText(view.context, "제목을 입력하세요", Toast.LENGTH_SHORT).show()
            } else if (content == "") {
                Toast.makeText(view.context, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
            } else {

                customProgressDialog.show()
                modifyBoard(id,userId,title, content)

            }

        }

        view.cancel_btn.setOnClickListener {
            val boardDetailFragment = BoardDetailFragment()
            val bundle = Bundle(1)
            bundle.putInt("id", id)
            bundle.putString("userId", userId)
            boardDetailFragment.arguments = bundle

            activity.supportFragmentManager.popBackStack(
                "boardModify",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            activity.supportFragmentManager.popBackStack(
                "boardDetail",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, boardDetailFragment)
                .addToBackStack("boardDetail")
                .commit()
        }

        return view
    }

    private fun modifyBoard(id:Int,userId:String,title: String, content: String) {
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

        boardService.modifyBoard(id,map,images)
            .enqueue(object : Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    if (response.isSuccessful) {
                        var result = response.body()
                        if (result != null) {
                            Log.i("결과 : ", "${result} ")
                            dialog.setPositiveButton(
                                "예",
                                DialogInterface.OnClickListener { _, _ ->
                                    customProgressDialog.dismiss()


                                    val boardDetailFragment = BoardDetailFragment()
                                    val bundle = Bundle(1)
                                    bundle.putInt("id", id)
                                    bundle.putString("userId", userId)
                                    boardDetailFragment.arguments = bundle
                                    var activity = (activity as MainActivity?)!!
                                    activity.supportFragmentManager.popBackStack("boardModify", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                    activity.supportFragmentManager.popBackStack("boardDetail", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                    activity.supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragment, boardDetailFragment)
                                        .addToBackStack("boardModify")
                                        .commit()
                                })
                            dialog.setCancelable(false)
                            dialog.setMessage("수정 되었습니다!").show()

                        }
                    }
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })


    }

    //이미지피커 갤러리 열기(내가 만든거임)
    private fun openImagePicker() {
        var intent = Intent(activity, GalleryActivity::class.java)
        intent.putExtra("size", boardPhotoList)
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

                    if (data != null) {
                        var result_list: java.util.ArrayList<Uri> =
                            data.getSerializableExtra("result_pic") as java.util.ArrayList<Uri>
                        for (uri in result_list) {
                            Log.i("결과", uri.toString())
                            boardPhotoList.add(uri)
                            adapter.notifyDataSetChanged()
                            var imagePath = getRealPathFormURI(uri)
                            destFiles?.add(File(imagePath))

                        }
                    }

                }
            }
        }
    }

    //이미지 파일 절대경로 가져오기(이미지를 파일형태로 가져오기 위해 절대경로가 필요함)
    private fun getRealPathFormURI(contentURI: Uri?): String {
        var result: String
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
                ContextCompat.checkSelfPermission(
                    activity as MainActivity,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED &&  //카메라 권한
                        ContextCompat.checkSelfPermission(
                            activity as MainActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED && //저장소 읽기
                        ContextCompat.checkSelfPermission(
                            activity as MainActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED //저장소 쓰기
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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).setOnKeyBackPressedListener(this)
    }

    override fun onBackKey() {
        val activity: MainActivity? = activity as MainActivity?
        activity?.setOnKeyBackPressedListener(null);
        activity?.onBackPressed()
    }


}