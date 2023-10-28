package com.example.testproject

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testproject.DTO.HomeResponseDTO
import com.example.testproject.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.converter.scalars.ScalarsConverterFactory


class MainActivity : AppCompatActivity() {

    val BASE_URL : String = "http://192.168.0.31:8080"
    lateinit var display : TextView

    /*
        Get Method Test
     */
    suspend fun getHome(){
        val retrofit : Retrofit =
            Retrofit.Builder()
            .baseUrl(BASE_URL)
            // response의 data를 Call<xxxDTO> 에 해당하는 xxxDTO로 바꾸어주는 converter 생성
            // 만약 Call<String>과 같은 구조라면 다른 Converter를 사용한다
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 참고 Call<String>인 경우에는 아래와 같은 형식으로 만듬
//        val gson = GsonBuilder().setLenient().create()
//        val retrofit : Retrofit =
//              Retrofit.Builder()
//                  .baseUrl(BASE_URL)
//                  .addConverterFactory(ScalarsConverterFactory.create(gson))
//                  .build()

        val networkService : InetworkService = retrofit.create(InetworkService::class.java)

        val homeCall = networkService.getHome2()

        // retrofit.create(InetworkService::class.java).getHome2().enqueue( object:.....) 이렇게
        //한줄로 적어도 된다

        // request를 asynchronously 실행시킨다
        homeCall.enqueue( object: Callback<HomeResponseDTO> {
            override fun onResponse(
                call: Call<HomeResponseDTO>,
                response: Response<HomeResponseDTO>
            ) {
                if(response.isSuccessful){
                    Log.i("$$ homecall success ", response.body().toString())
                    val result : HomeResponseDTO = response.body() as HomeResponseDTO
                    val mainScope = CoroutineScope(Dispatchers.Main)
                    mainScope.launch {
                        display.text = result.result
                    }
                } else {
                    var code = response.code()
                    Log.i("$$ homecall result resonse not ok", response.code().toString())
                }
            }

            override fun onFailure(call: Call<HomeResponseDTO>, t: Throwable) {
                Log.i("$$ homecall result onFailure", t.message.toString())
            }

        })
    }

    /*
       Post Method Test
    */
    suspend fun signon(){
        val retrofit : Retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val networkService : InetworkService = retrofit.create(InetworkService::class.java)

        val signOnCall = networkService.signon("Jones", "0000")

        // retrofit.create(InetworkService::class.java).signon("Jones", "0000").enqueue( object:.....) 이렇게
        //한줄로 적어도 된다

        // request를 asynchronously 실행시킨다
        signOnCall.enqueue( object: Callback<HomeResponseDTO> {
            override fun onResponse(
                call: Call<HomeResponseDTO>,
                response: Response<HomeResponseDTO>
            ) {
                if(response.isSuccessful){
                    Log.i("$$ homecall success ", response.body().toString())
                    val mainScope = CoroutineScope(Dispatchers.Main)
                    val result : HomeResponseDTO = response.body() as HomeResponseDTO
                    // UI를 변경하려면 main thread에서 실행
                    mainScope.launch {
                        display.text = result.result
                    }
                } else {
                    Log.i("$$ homecall result resonse not ok", response.code().toString())
                    Log.i("$$ homecall result resonse not ok", response.body().toString())
                    // UI를 변경하려면 main thread에서 실행
                    val mainScope = CoroutineScope(Dispatchers.Main)
                    mainScope.launch {
                        display.text = "Error code : " + response.code()
                    }
                }
            }

            override fun onFailure(call: Call<HomeResponseDTO>, t: Throwable) {
                Log.i("$$ homecall result onFailure", t.message.toString())
            }

        })
    }

    /*
    *   Authorization Header for HttpBasic test
     */
    suspend fun getSecured(){
        val retrofit : Retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val networkService : InetworkService = retrofit.create(InetworkService::class.java)

        // Authorization Header 작성       
        val credentials = "John:0000"
        val base64Credentials: String = "Basic " +
            Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        Log.i(" ### getSecured encoded Authorization Header", base64Credentials)

        // 나머지는 원래 대로, AuthorizationHeader 위치에 넣는다
        val getEnsureCall = networkService.getSecured(base64Credentials)

        // request를 asynchronously 실행시킨다
        getEnsureCall.enqueue( object: Callback<HomeResponseDTO> {
            override fun onResponse(
                call: Call<HomeResponseDTO>,
                response: Response<HomeResponseDTO>
            ) {
                if(response.isSuccessful){
                    Log.i("$$ homecall success ", response.body().toString())
                    val mainScope = CoroutineScope(Dispatchers.Main)
                    val result : HomeResponseDTO = response.body() as HomeResponseDTO
                    // UI를 변경하려면 main thread에서 실행
                    mainScope.launch {
                        display.text = result.result
                    }
                } else {
                    Log.i("$$ getEnsureCall result resonse not ok", response.code().toString())
                    Log.i("$$ getEnsureCall result resonse not ok", response.body().toString())
                    val mainScope = CoroutineScope(Dispatchers.Main)
                    // UI를 변경하려면 main thread에서 실행
                    mainScope.launch {
                        display.text = "Error code : " + response.code()
                    }
                }
            }

            override fun onFailure(call: Call<HomeResponseDTO>, t: Throwable) {
                Log.i("$$ getEnsureCall result onFailure", t.message.toString())
            }

        })
    }

    // multipart file upload 예제
    fun uploadMultipart(){
        val strFileName : String = "filename"
        var filePart: MultipartBody.Part? = null
        if (strFileName != null) {
            val fileDir = this.applicationContext.filesDir
            val upFile = File(fileDir, strFileName)
            val requestBodyFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), upFile)
            filePart = MultipartBody.Part.createFormData("file", upFile.name, requestBodyFile)
        }

        // Response가 Call<String>이라면 다음과 같이 ScalarConvertFactory로 convert
        //val gson = GsonBuilder().setLenient().create()
        val retrofit : Retrofit =
              Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .addConverterFactory(ScalarsConverterFactory.create())
                  .build()

        val networkService : InetworkService = retrofit.create(InetworkService::class.java)

        val uploadCall = networkService.createBoard("01012345678" ,
            "lee", "AUDIO",  filePart).execute()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindMain.root)

        display = bindMain.textView

        bindMain.buttonHome.setOnClickListener{
            //그냥 해도 될 수 있지만 결과가 오래 걸리면 ANR이 됨
            // 다른 thread를 만들어 실행시키고 함수는 suspended 선언
            val backgroundScope = CoroutineScope(Dispatchers.Default)
            backgroundScope.launch {
                getHome()
            }
        }

        bindMain.buttonSignon.setOnClickListener{
            //getHome() 그냥 해도 될 수 있지만 결과가 오래 걸리면 ANR이 됨
            // 다른 thread를 만들어 실행시키고 함수는 suspended 선언
            val backgroundScope = CoroutineScope(Dispatchers.Default)
            backgroundScope.launch {
                signon()
            }
        }

        bindMain.buttonLogin.setOnClickListener{
            //그냥 해도 될 수 있지만 결과가 오래 걸리면 ANR이 됨
            // 다른 thread를 만들어 실행시키고 함수는 suspended 선언
            val backgroundScope = CoroutineScope(Dispatchers.Default)
            backgroundScope.launch {
                getSecured()
            }
        }
    }
}