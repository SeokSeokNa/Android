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
        dialog.setTitle("??????")




        view.my_image_btn.setOnClickListener {
            if (checkPermission()) { //?????????????????? ??????????????? ????????? ??????
                //openGalleryForImage()
                openImagePicker()
            } else { //?????? ??????????????? ?????? ?????? ????????? ??????
                requestPermission()
            }

        }


        var activity: MainActivity
        activity = (getActivity() as MainActivity?)!!
        var recyclerView = view.upload_photo_list

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        //???????????? ????????? ?????????
        var div_deco = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        recyclerView.addItemDecoration(div_deco)

        //????????? ?????? ??????
        customProgressDialog = ProgressDialog(view.context)
        //????????? ????????????
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.modify_btn.setOnClickListener {
            var title = view.board_title.text.toString()
            var content = view.board_content.text.toString()

            if (title == "") {
                Toast.makeText(view.context, "????????? ???????????????", Toast.LENGTH_SHORT).show()
            } else if (content == "") {
                Toast.makeText(view.context, "????????? ???????????????", Toast.LENGTH_SHORT).show()
            } else {

                customProgressDialog.show()
                modifyBoard(id,userId,title, content)

            }

        }

        view.cancel_btn.setOnClickListener {
            onBackKey();
        }

        return view
    }

    private fun modifyBoard(id:Int,userId:String,title: String, content: String) {
        if (destFiles?.size !=0) {

            for (file in destFiles!!) {
                // RequestBody??? ??????
                var requestBmp = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                // MultipartBody.Part??? ?????? ?????????
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
                            Log.i("?????? : ", "${result} ")
                            dialog.setPositiveButton(
                                "???",
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
                            dialog.setMessage("?????? ???????????????!").show()

                        }
                    }
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })


    }

    //??????????????? ????????? ??????(?????? ????????????)
    private fun openImagePicker() {
        var intent = Intent(activity, GalleryActivity::class.java)
        intent.putExtra("size", boardPhotoList)
        startActivityForResult(intent, REQUEST_GALLERY_TAKE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("??????", requestCode.toString())
        when (requestCode) {
            0 -> {
                Toast.makeText(activity as MainActivity, "?????? ?????? ??????", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                if (requestCode == REQUEST_GALLERY_TAKE && resultCode == Activity.RESULT_OK) {

                    if (data != null) {
                        var result_list: java.util.ArrayList<Uri> =
                            data.getSerializableExtra("result_pic") as java.util.ArrayList<Uri>
                        for (uri in result_list) {
                            Log.i("??????", uri.toString())
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

    //????????? ?????? ???????????? ????????????(???????????? ??????????????? ???????????? ?????? ??????????????? ?????????)
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

    //????????? ?????? ?????? (????????? ?????? ??? ????????? ????????? ?????? ??????)
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

    //????????? ?????? ??????( ????????? ?????? ??? ????????? ????????? ?????? ??????)
    private fun checkPermission(): Boolean {
        return (
                ContextCompat.checkSelfPermission(
                    activity as MainActivity,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED &&  //????????? ??????
                        ContextCompat.checkSelfPermission(
                            activity as MainActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED && //????????? ??????
                        ContextCompat.checkSelfPermission(
                            activity as MainActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED //????????? ??????
                )
    }

    //???????????? ??????
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(
                "TAG",
                "Permission: " + permissions[0] + "was" + grantResults[0] + "????????? ?????? ?????? ??????^^"
            )
        } else {
            Log.d("TAG", "????????? ?????? ????????? ??? ??????!!")
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