package org.rmatil.sync.client.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyPairUtils {

    public static RSAPrivateKey readPrivateKey(String path)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path pathToPrivateKey = Paths.get(path);

        if (! pathToPrivateKey.toFile().exists()) {
            throw new IllegalArgumentException("Path " + path + " does not exist");
        }

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Files.readAllBytes(pathToPrivateKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(privateKeySpec);
    }

    public static RSAPublicKey readPublicKey(String path)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        Path pathToPublicKey = Paths.get(path);

        if (! pathToPublicKey.toFile().exists()) {
            throw new IllegalArgumentException("Path " + path + " does not exist");
        }

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Files.readAllBytes(pathToPublicKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(publicKeySpec);
    }

    public static void writePrivateKey(RSAPrivateKey privateKey, String path)
            throws IOException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());

        Path pathToPrivateKey = Paths.get(path);

        Files.write(pathToPrivateKey, pkcs8EncodedKeySpec.getEncoded());
    }

    public static void writePublicKey(RSAPublicKey publicKey, String path)
            throws IOException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());

        Path pathToPrivateKey = Paths.get(path);

        Files.write(pathToPrivateKey, x509EncodedKeySpec.getEncoded());
    }
}
