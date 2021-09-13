package com.acaroom.apicallpjt.data_domain

class BoardListDto(val count:Int,
                   val hasNext:Boolean,
                   val data:List<BoardDto>
                   ) {
}