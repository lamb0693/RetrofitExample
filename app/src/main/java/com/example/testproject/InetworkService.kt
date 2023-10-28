package com.example.testproject

import com.example.testproject.DTO.HomeResponseDTO
import com.example.testproject.DTO.ListResultDTO
import com.example.testproject.DTO.LoginResponseDTO
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface InetworkService {
    //    Server 구조
//    @RequestBody
//    @GetMapping("/")
//    public Sting hello(){ }
    @GET("/")
    fun getHome():retrofit2.Call<String>

//        Server 구조
//    @RequestBody
//    @GetMapping("/home")
//    public <HomeResponseDTO> hello2(){ }
    @GET("/")
    fun getHome2():retrofit2.Call<HomeResponseDTO>

//    Server 구조
//    @RequestBody
//    @PostMapping("/auth/login")
//    public <LoginResponseDTO> login(@RequestBody LoginDTO dto){ }
    @Headers("Content-Type: application/json") // 넣어야 되는지 안 넣어야 하는지 시험
    @POST("/auth/login")
    fun login(
        @Body param : Map<String, String>
    ): retrofit2.Call<LoginResponseDTO>

//    Server 구조
//    @PostMapping(value="/signon")
//    @ResponseBody
//    public ResponseEntity<ResultDTO> register(@RequestParam String username, @RequestParam String password){
    @FormUrlEncoded
    @POST("/signon")
    fun signon(
        // parameter
        @Field("username") username: String,
        @Field("password") password: String
    ): retrofit2.Call<HomeResponseDTO>

//    @GetMapping("/secured")
//    @ResponseBody
//    public ResponseEntity<ResultDTO> displaySecured(){
//        return ResponseEntity.ok().body(new ResultDTO("secured Page"));
//    }
    @Headers("Content-Type: application/json") // 넣어야 되는지 안 넣어야 하는지 시험
    @GET("/secured")
    fun getSecured(
        @Header("Authorization") authorizationHeader: String
    ): retrofit2.Call<HomeResponseDTO>

    //    Server 구조
//    @RequestBody
//    @PostMapping("/signon")
//    public <ListResultDTO> login(@RequestParam String tel, @RequestParam int count){ }
    @FormUrlEncoded
    @POST("/api/board/list")
    fun listBoard(
        //Header가 필요한 경우
        @Header("Authorization") authToken: String?,
        // parameter
        @Field("tel") tel: String?,
        @Field("count") count: Int
    ): retrofit2.Call<List<ListResultDTO>>

    //    Server 구조 Mutlipart 포함 e.g upload
//    @RequestBody
//    @PostMapping("/api/board/create")
//    public <ListResultDTO> createBoard(@RequestParam String customoreTel,
//      @RequestParam String content, @ReuestParam String message,  ...........){ }
    @Multipart
    @POST("/api/board/create")
    fun createBoard(
        //@Header("Authorization") authToken: String,
        @Part("customerTel") customerTel: String,
        @Part("content") content: String,
        @Part("message") message: String,
        @Part file: MultipartBody.Part?
    ): retrofit2.Call<String>
}