package com.acaroom.apicallpjt.data_domain

data class BoardDto(val id:String,
                    val title:String,
                    val contents:String,
                    val writeDate:String,
                    val hit:String,
                    val userName:String,
                    val userId:String,
                    val photoList:List<PhotoDto>)
