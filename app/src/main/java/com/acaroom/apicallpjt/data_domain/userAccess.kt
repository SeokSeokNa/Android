package com.acaroom.apicallpjt.data_domain

data class userAccess(var accessToken: String, var refreshToken: String,var tokenType:String, var userName:String , var userId:String, var expireDate:Long){

}
