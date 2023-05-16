package com.example.kryptozad2;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HexFormat;


public class ElGamal {
    private BigInteger p;
    private BigInteger k;
    private BigInteger h;
    private BigInteger g;

    private BigInteger Nm1;

    private BigInteger c1;

    private BigInteger c2;

    private String pKey = "";

    private String gKey = "";

    private String hKey = "";

    private String privateKey = "";

    public void setP(BigInteger p) {
        this.p = p;
    }

    public void setH(BigInteger h) {
        this.h = h;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public void setC1(BigInteger c1) {
        this.c1 = c1;
    }

    public void setC2(BigInteger c2) {
        this.c2 = c2;
    }

    public void generateKeys() {
        p = BigInteger.probablePrime(512, new SecureRandom());
        g = new BigInteger(510, new SecureRandom());
        k = new BigInteger(510, new SecureRandom()); // to tylko prywatny
        // N == p // g == g // a == k //
        h = g.modPow(k, p);
        Nm1 = p.subtract(BigInteger.ONE);


        byte[] t1 = p.toByteArray();
        byte[] t2 = g.toByteArray();
        byte[] t3 = h.toByteArray();
        byte[] t4 = k.toByteArray();
        HexFormat hex = HexFormat.of();
        pKey = hex.formatHex(t1);
        gKey = hex.formatHex(t2);
        hKey = hex.formatHex(t3);
        privateKey = hex.formatHex(t4);

    }

    public String[] encryptMessage(byte[] toEncrypt) {

        BigInteger b = new BigInteger(500, new SecureRandom());
        if(Nm1 == null) {
            Nm1 = p.subtract(BigInteger.ONE);
        }

        while (true) {
            if (b.gcd(Nm1).equals(BigInteger.ONE)) {
                break;
            }
            b = BigInteger.probablePrime(500, new SecureRandom());
        }

        BigInteger temp = new BigInteger(toEncrypt);
        BigInteger c1 = g.modPow(b, p);
        BigInteger c2 = h.modPow(b,p);
        c2 = c2.multiply(temp);
        String[] parts = new String[2];
        parts[0] = c1.toString();
        parts[1] = c2.toString();
        return parts;
    }
    public boolean verifyKeys() {

        if(h.equals(g.modPow(k, p))) {
            return true;
        }
        return false;
    }

    public byte[] decryptMessage() {

        BigInteger temp = c1.modPow(k,p);
        BigInteger temp2 = c2.divide(temp);

        byte[] test = temp2.toByteArray();

        return test;
    }

    public String getpKey() {
        return pKey;
    }

    public String getgKey() {
        return gKey;
    }

    public String gethKey() {
        return hKey;
    }


    public void setPrivateKey(BigInteger tt) {
        this.k = tt;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
