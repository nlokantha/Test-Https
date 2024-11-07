package com.example.testhttps;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkClient {
    private final OkHttpClient client;
    public NetworkClient(Context context) {
        client = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory(context), getTrustManager())
                .build();
    }

    private SSLSocketFactory createSSLSocketFactory(Context context) {
        try {
            // Load CAs from an InputStream
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            // Load the certificate from the raw resource file
            InputStream inputStream = context.getResources().openRawResource(R.raw.combank_ca);
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            // Create a KeyStore containing the trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private X509TrustManager getTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            return (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String run(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }
}
