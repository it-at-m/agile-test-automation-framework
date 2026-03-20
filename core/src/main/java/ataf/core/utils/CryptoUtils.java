package ataf.core.utils;

import ataf.core.assertions.CustomAssertions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility class for AES-256 encryption and decryption of text and files.
 *
 * <div>
 * This class uses AES in GCM mode with no padding to provide authenticated encryption.
 * A secret must be configured before calling any encryption or decryption method.
 * </div>
 *
 * <div>
 * The secret and encrypted file extension are stored in static fields because this class
 * exposes a static utility API. Access to mutable shared state is synchronized to ensure
 * thread safety and memory visibility across concurrent test execution.
 * </div>
 *
 * <div>
 * For security reasons, the configured secret is defensively copied when set and cleared
 * from memory when removed.
 * </div>
 */
public final class CryptoUtils {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;

    private static final Object LOCK = new Object();

    private static char[] secret;
    private static String encryptedFileExtension = ".encrypted";

    private CryptoUtils() {
        // Utility class
    }

    /**
     * Generates a random nonce of the given size.
     *
     * @param numBytes the number of random bytes to generate
     * @return a byte array containing random bytes
     * @throws IllegalArgumentException if {@code numBytes <= 0}
     */
    private static byte[] getRandomNonce(int numBytes) {
        if (numBytes <= 0) {
            throw new IllegalArgumentException("Nonce size must be positive.");
        }

        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    /**
     * Creates an AES-256 key derived from the currently configured secret and the given salt.
     *
     * <div>
     * A defensive snapshot of the configured secret is taken under synchronization to avoid
     * race conditions and to ensure a stable value is used for key derivation.
     * </div>
     *
     * @param salt the salt used for PBKDF2 key derivation
     * @return the derived AES key
     * @throws NoSuchAlgorithmException if the key derivation algorithm is not available
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws IllegalArgumentException if no secret has been configured
     */
    private static SecretKey getAESKey(byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] secretSnapshot;

        synchronized (LOCK) {
            CustomAssertions.assertFalse(ArrayUtils.isEmpty(secret), "Cannot create AES key as no secret was given.");
            secretSnapshot = Arrays.copyOf(secret, secret.length);
        }

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretSnapshot, salt, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } finally {
            Arrays.fill(secretSnapshot, '\0');
        }
    }

    /**
     * Returns the currently configured encrypted file extension.
     *
     * @return the encrypted file extension
     */
    private static String getEncryptedFileExtension() {
        synchronized (LOCK) {
            return encryptedFileExtension;
        }
    }

    /**
     * Encrypts the given plain text using AES-256 and returns the result as Base64.
     *
     * @param plainText the plain text to encrypt
     * @return the encrypted text encoded as Base64
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws NoSuchPaddingException if the padding scheme is not available
     * @throws InvalidAlgorithmParameterException if the algorithm parameters are invalid
     * @throws InvalidKeyException if the encryption key is invalid
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if the block size is invalid
     */
    public static String encrypt(String plainText)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

        SecretKey aesKeyFromPassword = getAESKey(salt);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array();

        return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
    }

    /**
     * Decrypts the given Base64-encoded encrypted text using AES-256.
     *
     * @param encryptedText the encrypted text encoded as Base64
     * @return the decrypted plain text
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws NoSuchPaddingException if the padding scheme is not available
     * @throws InvalidAlgorithmParameterException if the algorithm parameters are invalid
     * @throws InvalidKeyException if the decryption key is invalid
     * @throws BadPaddingException if authentication or padding validation fails
     * @throws IllegalBlockSizeException if the block size is invalid
     */
    public static String decrypt(String encryptedText)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        byte[] decoded = Base64.getDecoder().decode(encryptedText.getBytes(StandardCharsets.UTF_8));

        ByteBuffer bb = ByteBuffer.wrap(decoded);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);

        byte[] salt = new byte[SALT_LENGTH_BYTE];
        bb.get(salt);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        SecretKey aesKeyFromPassword = getAESKey(salt);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }

    /**
     * Encrypts the given file using AES-256 and appends the configured encrypted file extension.
     *
     * @param inputFile the file to encrypt
     * @return the absolute path to the encrypted file
     * @throws IOException if an I/O error occurs
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws NoSuchPaddingException if the padding scheme is not available
     * @throws InvalidAlgorithmParameterException if the algorithm parameters are invalid
     * @throws InvalidKeyException if the encryption key is invalid
     * @throws BadPaddingException if padding is invalid
     * @throws IllegalBlockSizeException if the block size is invalid
     */
    public static Path encryptFile(Path inputFile)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        String fileExtension = getEncryptedFileExtension();
        Path outputFile = inputFile.resolveSibling(inputFile.getFileName().toString() + fileExtension);

        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

        SecretKey aesKey = getAESKey(salt);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        try (FileInputStream fis = new FileInputStream(inputFile.toFile());
                FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {

            fos.write(iv);
            fos.write(salt);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] encryptedChunk = cipher.update(buffer, 0, bytesRead);
                if (encryptedChunk != null) {
                    fos.write(encryptedChunk);
                }
            }

            byte[] finalChunk = cipher.doFinal();
            if (finalChunk != null) {
                fos.write(finalChunk);
            }
        }

        return outputFile.toAbsolutePath();
    }

    /**
     * Decrypts the given encrypted file and removes the configured encrypted file extension.
     *
     * @param encryptedFile the encrypted file to decrypt
     * @return the absolute path to the decrypted file
     * @throws IOException if an I/O error occurs
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws NoSuchPaddingException if the padding scheme is not available
     * @throws InvalidAlgorithmParameterException if the algorithm parameters are invalid
     * @throws InvalidKeyException if the decryption key is invalid
     * @throws BadPaddingException if authentication or padding validation fails
     * @throws IllegalBlockSizeException if the block size is invalid
     */
    public static Path decryptFile(Path encryptedFile)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Path outputFile = getOutputFile(encryptedFile);

        Path parentDir = outputFile.getParent();
        Path tempFile = Files.createTempFile(
                parentDir != null ? parentDir : Path.of("."),
                outputFile.getFileName().toString(),
                ".tmp");

        try (FileInputStream fis = new FileInputStream(encryptedFile.toFile());
                FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {

            byte[] iv = new byte[IV_LENGTH_BYTE];
            if (fis.read(iv) != iv.length) {
                throw new IOException("Invalid encrypted file: IV missing or incomplete.");
            }

            byte[] salt = new byte[SALT_LENGTH_BYTE];
            if (fis.read(salt) != salt.length) {
                throw new IOException("Invalid encrypted file: salt missing or incomplete.");
            }

            SecretKey aesKey = getAESKey(salt);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] decryptedChunk = cipher.update(buffer, 0, bytesRead);
                if (decryptedChunk != null) {
                    fos.write(decryptedChunk);
                }
            }

            byte[] finalChunk = cipher.doFinal();
            if (finalChunk != null) {
                fos.write(finalChunk);
            }
        } catch (Exception e) {
            Files.deleteIfExists(tempFile);
            throw e;
        }

        Files.move(
                tempFile,
                outputFile,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);

        return outputFile.toAbsolutePath();
    }

    /**
     * Resolves the output file path for a decrypted file by removing the configured encrypted file
     * extension
     * from the given file name.
     *
     * <p>
     * The given file name must end with the currently configured encrypted file extension. Otherwise,
     * an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param encryptedFile the encrypted input file
     * @return the resolved output file path with the encrypted file extension removed
     * @throws IllegalArgumentException if the file name does not end with the expected encrypted file
     *             extension
     */
    private static @NotNull
    Path getOutputFile(Path encryptedFile) {
        String fileExtension = getEncryptedFileExtension();
        String encryptedFileName = encryptedFile.getFileName().toString();

        if (!encryptedFileName.endsWith(fileExtension)) {
            throw new IllegalArgumentException("File does not have the expected extension: " + fileExtension);
        }

        String originalFileName = encryptedFileName.substring(0, encryptedFileName.length() - fileExtension.length());
        return encryptedFile.resolveSibling(originalFileName);
    }

    /**
     * Sets the file extension used for encrypted files.
     *
     * @param encryptedFileExtension the file extension to use; must not be {@code null} or blank
     * @throws IllegalArgumentException if the given extension is {@code null} or blank
     */
    public static void setCustomFileExtension(String encryptedFileExtension) {
        CustomAssertions.assertFalse(
                encryptedFileExtension == null || encryptedFileExtension.isBlank(),
                "Encrypted file extension must not be null or blank.");

        synchronized (LOCK) {
            CryptoUtils.encryptedFileExtension = encryptedFileExtension;
        }
    }

    /**
     * Sets the secret used for encryption and decryption.
     *
     * <p>
     * The provided array is defensively copied so later modifications by the caller do not affect
     * the stored secret.
     * </p>
     *
     * @param secret the secret to use
     * @throws IllegalArgumentException if the given secret is {@code null} or empty
     */
    public static void setSecret(char[] secret) {
        CustomAssertions.assertFalse(ArrayUtils.isEmpty(secret), "Secret must not be null or empty.");

        synchronized (LOCK) {
            clearSecretInternal();
            CryptoUtils.secret = Arrays.copyOf(secret, secret.length);
        }
    }

    /**
     * Clears the currently configured secret from memory.
     *
     * <p>
     * The internal secret array is overwritten with null characters before the reference is removed.
     * </p>
     */
    public static void clearSecret() {
        synchronized (LOCK) {
            clearSecretInternal();
            secret = null;
        }
    }

    /**
     * Overwrites the currently stored secret, if present.
     */
    private static void clearSecretInternal() {
        if (secret != null) {
            Arrays.fill(secret, '\0');
        }
    }
}
