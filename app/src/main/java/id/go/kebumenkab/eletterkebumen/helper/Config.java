package id.go.kebumenkab.eletterkebumen.helper;

import id.go.kebumenkab.eletterkebumen.network.pns.ApiClient;

public class Config {
    public static final String URL_CEK_DELEGASI = ApiClient.DOMAIN +"/api/instansi/status_delegasi";
    public static final String URL_AJUDAN_ORDER_MASUK = ApiClient.DOMAIN +"/api/index.php/suratinternalprocess/order_ajudan_masuk/";
    public static final String URL_AJUDAN_ORDER_KONSEP = ApiClient.DOMAIN +"/api/index.php/suratinternalprocess/order_ajudan/";

    public static final String VERSIONS_FILE_URL = ApiClient.DOMAIN +"/api/index.php/auth/cek/";

    /** Eletter Desa **/
    public static final String AUTHORIZATION = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjMiLCJ1c2VybmFtZSI6ImVsZXR0ZXIiLCJpYXQiOjE2MTk1NzU3NzN9.2I0ZOsEnKxaEsKdhnNQI70Cm-kg4z0L5GOfco8Q_Noo";
    public static final String URL_LOGIN_DESA ="https://desaonline.kebumenkab.go.id/api/login/";
}
