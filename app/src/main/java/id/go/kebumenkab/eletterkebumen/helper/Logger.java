package id.go.kebumenkab.eletterkebumen.helper;

import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import id.go.kebumenkab.eletterkebumen.model.DataItemSuratMasuk;
import id.go.kebumenkab.eletterkebumen.model.ResponStandar;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDiajukan;
import id.go.kebumenkab.eletterkebumen.model.ResultArsipDisetujui;
import id.go.kebumenkab.eletterkebumen.model.ResultDetail;
import id.go.kebumenkab.eletterkebumen.model.ResultHistoriArsip;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsep;
import id.go.kebumenkab.eletterkebumen.model.ResultKonsepDesa;
import id.go.kebumenkab.eletterkebumen.model.ResultPemberitahuan;
import id.go.kebumenkab.eletterkebumen.model.ResultSuratDitandatangani;
import id.go.kebumenkab.eletterkebumen.model.ResultTindakan;
import retrofit2.Response;

public class Logger {

    public Logger(){

    }

    public void d(String title, String message){

        Log.d(Tag.TAG_APLIKASI, title+ " : " + message);
    }

    public void e(String title,String message){

          Log.e(Tag.TAG_APLIKASI, title+ " : " +message);
    }

    public void w(String title, Response<ResultKonsep> response){
       //   Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void wd(String title, Response<ResultKonsepDesa> response){
        //Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w2(String title, List<DataItemSuratMasuk> response){
       //  Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w3(String title, Response<ResultPemberitahuan> response){
       // Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w4(String title, Response<ResponStandar> response){
      Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }
    public void w5(String title, Response<ResultDetail> response){
       // Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w6(String title, Response<ResultTindakan> response){
      //  Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w7(String title, Response<ResultHistoriArsip> response){
      //  Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w8(String title, Response<ResultArsipDiajukan> response){
       // Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w9(String title, Response<ResultArsipDisetujui> response){
      //  Log.w(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

    public void w10(String title, Response<ResultKonsepDesa> response){
       // Log.d(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }
    public void w11(String title, Response<ResultSuratDitandatangani> response){
      //  Log.d(Tag.TAG_APLIKASI, title+ " : " + new Gson().toJson(response));
    }

}
