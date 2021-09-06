package com.acaroom.apicallpjt.data_domain

data class userAccess(var accessToken: String, var refreshToken: String,var tokenType:String, var userName:String , var expireDate:Long){

}
