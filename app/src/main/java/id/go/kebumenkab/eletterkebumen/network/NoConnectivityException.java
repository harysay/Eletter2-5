package id.go.kebumenkab.eletterkebumen.network;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "Silakan periksa koneksi internet Anda";
    }

}