package id.go.kebumenkab.eletterkebumen.helper;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.model.KonsepDesa;


public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    private List<String> stringList;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "eletter";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_LOGGED_IN = "IsLoggedIn";

    private static final String SESSION_ID_PNS       = "id_pns";
    private static final String SESSION_NIP          = "nip";
    private static final String SESSION_NAMA         = "nama";

    private static final String SESSION_NIK          = "nik";

    private static final String SESSION_ID_UNIT      = "id_unit_kerja";
    private static final String SESSION_UNIT         = "unit_kerja";
    private static final String SESSION_ID_JABATAN   = "id_jabatan";
    private static final String SESSION_JABATAN      = "id_pns";
    private static final String JABATAN                 = "jabatan";
    private static final String SESSION_TOKEN        = "token";
    private static final String SESSION_STATUS_JABATAN = "status_jabatan";
    private static final String SESSION_USERNAME     = "username";
    private static final String SESSION_STATUS       = "status";
    private static final String SESSION_DETAIL       = "detail";

    private static final String SESSION_TYPE       = "type";
    private static final String SESSION_ARSIPARIS      = "arsip_aris";

    private static final String SESSION_DEVICE       = "device";


    private static final String KEY_ACCESS_TOKEN_FIREBASE = "token_firebase";



    private static  String ID_PERANGKAT = "id_perangkat";
    private static  String URL_DESA = "url";
    private static  String SESSION_BOOKMARK = "bookmark";

    private static PrefManager mInstance;

    private String nama;

    public String getNama() {
        return pref.getString(SESSION_NAMA, "");
    }

    public void setNama(String nama) {
        editor.putString(SESSION_NAMA, nama);
        editor.commit();
    }


    public String getJabatan() {
        return pref.getString(SESSION_JABATAN, "");
    }

    public void setJabatan(String jabatan) {
        editor.putString(SESSION_JABATAN, jabatan);
        editor.commit();
    }

    private String jabatan;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized PrefManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new PrefManager(context);
        }
        return mInstance;
    }

    /** Desa **/
    public String getIdPerangkat() {
        return pref.getString(ID_PERANGKAT, "");
    }

    public void setIdPerangkat(String idPerangkat){
        editor = pref.edit();
        editor.putString(ID_PERANGKAT, idPerangkat);
        editor.apply();
    }

    public String getUrlDesa() {
        return pref.getString(URL_DESA, "");
    }

    public void setUrlDesa(String urlDesa) {
        editor = pref.edit();
        editor.putString(URL_DESA, urlDesa);
        editor.apply();

    }

    /** Batas Konfigurasi Desa **/

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor = pref.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }
    public void setIsLoggedIn(boolean isLoggedIn) {
        editor = pref.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public void simpanUsername(String username){
        editor = pref.edit();
        editor.putString(SESSION_USERNAME, username);
        editor.apply();
    }

    public String getUsername(){
        return pref.getString(SESSION_USERNAME,"");
    }

    public void simpanNIK(String nik){
        editor = pref.edit();
        editor.putString(SESSION_NIK, nik);
        editor.apply();
    }

    public String getNIK(){
        return pref.getString(SESSION_NIK,"");
    }

    public void simpanNIP(String nip){
        editor = pref.edit();
        editor.putString(SESSION_NIP, nip);
        editor.apply();
    }

    public String getNIP(){
        return pref.getString(SESSION_NIP,"");
    }



    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public String getSessionIdUnit() {
        return pref.getString(SESSION_ID_UNIT, "");
    }

    public void setSession(String nama,
                           String nip,
                           String id_pns,
                           String id_unit,
                           String unit_kerja,
                           String id_jabatan,
                           String jabatan,
                           String status_jabatan,
                           String token, String device){
        editor = pref.edit();
        editor.putString(SESSION_NAMA, nama);
        editor.putString(SESSION_ID_PNS, id_pns);
        editor.putString(SESSION_NIP, nip);
        editor.putString(SESSION_ID_UNIT, id_unit);
        editor.putString(SESSION_UNIT, unit_kerja);
        editor.putString(SESSION_ID_JABATAN, id_jabatan);
        editor.putString(SESSION_JABATAN, jabatan);
        editor.putString(SESSION_STATUS_JABATAN, status_jabatan);
        editor.putString(SESSION_TOKEN, token);
        editor.putString(SESSION_DEVICE, device);


        editor.apply();
    }

    public String getSessionNama(){
        return pref.getString(SESSION_NAMA,"");
    }

    public String getSessionJabatan(){
        return pref.getString(SESSION_JABATAN,"");
    }

    public String getStatusJabatan () { return pref.getString(SESSION_STATUS_JABATAN,"");}

    public String getSessionUnit(){
        return pref.getString(SESSION_UNIT, "");
    }

    public String getSessionToken(){
       return pref.getString(SESSION_TOKEN,"");
    }

    public String getSessionDevice(){
        return pref.getString(SESSION_DEVICE,"");
    }

    public  String getSessionIdJabatan(){return pref.getString(SESSION_ID_JABATAN, "");}

    public void  clearSession(){
        editor = pref.edit();

        editor.putString(SESSION_NAMA, "");
        editor.putString(SESSION_ID_PNS, "");
        editor.putString(SESSION_NIP, "");
        editor.putString(SESSION_ID_UNIT, "");
        editor.putString(SESSION_UNIT, "");
        editor.putString(SESSION_ID_JABATAN, "");
        editor.putString(SESSION_JABATAN, "");
        editor.putString(SESSION_STATUS_JABATAN, "");
        editor.putString(SESSION_TOKEN, "");
        editor.putString(SESSION_DEVICE, "");

        editor.putString(ID_PERANGKAT, "");
        editor.putString(URL_DESA, "");
        editor.putString(SESSION_TYPE, "");
        editor.putString(SESSION_ARSIPARIS, "");
        editor.putString(SESSION_NIK, "");

        editor.apply();
    }

    public boolean storeTokenFirebase(String token){
        editor = pref.edit();
        editor.putString(KEY_ACCESS_TOKEN_FIREBASE, token);
        editor.apply();
        return true;
    }

    public boolean simpanTipeDanArsipAris(String type, String arsipAris){
        editor = pref.edit();
        editor.putString(SESSION_TYPE, type);
        editor.putString(SESSION_ARSIPARIS, arsipAris);
        editor.apply();
        return true;
    }
    public String getType(){
        return pref.getString(SESSION_TYPE, "");
    }
    public String getSessionArsiparis(){
        return pref.getString(SESSION_ARSIPARIS, "");
    }

    public String getTokenFirebase(){
        return pref.getString(KEY_ACCESS_TOKEN_FIREBASE, null);
    }

    public ArrayList<String> ambilDataKonsepList(){
        ArrayList<String> data = new ArrayList<>();
        Gson gson = new Gson();
        String json = pref.getString("list-id", "");
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(!json.equals("")){
            data = gson.fromJson(json, type);
        }
        return data;
    }

    public void tambahkanDataKonsepList(String idKonsepDesa){
        List<String> stringList = new ArrayList<String>();
        stringList = ambilDataKonsepList();
        stringList.add(idKonsepDesa);
        simpanDataKonsepList(stringList);
    }

    public void hilangkanDataKonsepList(String idKonsepDesa){
        List<String> stringList = new ArrayList<String>();
        stringList = ambilDataKonsepList();
        stringList.remove(idKonsepDesa);
        simpanDataKonsepList(stringList);

    }

    private void simpanDataKonsepList(List<String> listKonsepDesa){
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listKonsepDesa);
        editor.putString("list-id", ""); //kosongkan
        editor.putString("list-id", json);
        editor.apply();
    }
}
