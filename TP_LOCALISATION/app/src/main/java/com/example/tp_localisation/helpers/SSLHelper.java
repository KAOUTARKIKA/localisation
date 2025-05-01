package com.example.tp_localisation.helpers;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;

public class SSLHelper {

    private static X509TrustManager trustManager = null;

    public static X509TrustManager getTrustManager() {
        if (trustManager == null) {
            trustManager = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                }
            };
        }
        return trustManager;
    }

    public static SSLSocketFactory getUnSafeSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCertificates = new TrustManager[]{ getTrustManager() };

        // Install the all-trusting trust manager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustAllCertificates, new SecureRandom());

        // Return the SSLSocketFactory object
        return context.getSocketFactory();
    }
}