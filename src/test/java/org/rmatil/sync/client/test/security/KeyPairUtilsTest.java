package org.rmatil.sync.client.test.security;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rmatil.sync.client.security.KeyPairUtils;
import org.rmatil.sync.client.util.FileUtil;
import org.rmatil.sync.core.exception.InitializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertArrayEquals;

public class KeyPairUtilsTest {

    private static final Path ROOT_TEST_FOLDER = Paths.get("./test-folder");
    private static final Path PRIVATE_KEY_FILE = ROOT_TEST_FOLDER.resolve("id_rsa");
    private static final Path PUBLIC_KEY_FILE  = ROOT_TEST_FOLDER.resolve("id_rsa.pub");

    private static RSAPrivateKey privateKey;
    private static RSAPublicKey  publicKey;


    @BeforeClass
    public static void setUp()
            throws IOException {
        if (! ROOT_TEST_FOLDER.toFile().exists()) {
            Files.createDirectories(ROOT_TEST_FOLDER);
        }

        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new InitializationException(e);
        }

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    @AfterClass
    public static void tearDown() {
        FileUtil.delete(ROOT_TEST_FOLDER.toFile());
    }

    @Test
    public void testPrivate()
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        KeyPairUtils.writePrivateKey(privateKey, PRIVATE_KEY_FILE.toString());
        RSAPrivateKey result = KeyPairUtils.readPrivateKey(PRIVATE_KEY_FILE.toString());

        assertArrayEquals("Private key bytes are not equals", privateKey.getEncoded(), result.getEncoded());
    }

    @Test
    public void testPublic()
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        KeyPairUtils.writePublicKey(publicKey, PUBLIC_KEY_FILE.toString());
        RSAPublicKey result = KeyPairUtils.readPublicKey(PUBLIC_KEY_FILE.toString());

        assertArrayEquals("Public key bytes are not equals", publicKey.getEncoded(), result.getEncoded());
    }
}

