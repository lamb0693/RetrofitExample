package com.example.testproject.DTO
// 변수 이름 서버와 일치
data class LoginResponseDTO(
    var name : String,
    var password : String,
    //@SerializedName("name") var strName : String
)
