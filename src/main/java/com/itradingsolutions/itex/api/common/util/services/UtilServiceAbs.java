package com.itradingsolutions.itex.api.common.util.services;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.service.IMessageService;
import com.itradingsolutions.itex.api.common.util.exceptions.NotEncryptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.ZoneId;
import java.util.Base64;

public abstract class UtilServiceAbs {

    @Value("${itex.security.encrypt.key}")
    private String encryptTextPass;
    private static final String ENCRYPTION_ALG = "AES";

    protected ZoneId zoneId = ZoneId.of("America/New_York");

    @Value("${itex.tabs.max-tabs-open}")
    protected Integer maxTabsOpen;

    protected static final String NOT_FOUND_MESSAGE = "Not found";

    @Autowired
    private IMessageService messageService;

    protected String simpleMessage(String template) {
        return messageService.simpleMessage(template);
    }

    protected String compositeMessage(String template, String[] attributes) {
        return messageService.compositeMessage(template, attributes);
    }

    protected String encryptText(String text){
        try {
            SecretKey key = generateKey();
            Cipher encryptor = Cipher.getInstance(ENCRYPTION_ALG);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            byte[] textEncrypt = encryptor.doFinal(text.trim().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(textEncrypt);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
            throw new NotEncryptException("Problems encrypting text", ex);
        }
    }

    protected String decryptText(String text) {
        try {
            SecretKey key = generateKey();
            Cipher encryptor = Cipher.getInstance(ENCRYPTION_ALG);
            encryptor.init(Cipher.DECRYPT_MODE, key);
            byte[] textDecrypt = encryptor.doFinal(Base64.getDecoder().decode(text));
            return new String(textDecrypt, StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new NotEncryptException("Problems decrypting text", ex);
        }
    }

    protected String capitalizeName(String name) {
        if (name == null || name.isEmpty()) return name;
        StringBuilder resp = new StringBuilder();
        var names =  name.toLowerCase().trim().split(" ");
        for (String newName: names) {
            resp.append(StringUtils.capitalize(newName)).append(" ");
        }
        return resp.toString().trim();
    }

    protected String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    protected static String normalizeText(String input) {
        if (input == null || input.isEmpty()) return input;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
    }

    public static String removeSpecialChars(String input) {
        String regex = "[~!@#$%^*\\\\{}\\[\\];:?<>_]";
        return input.replaceAll(regex, "");
    }

    private SecretKey generateKey() {
        int maxLength = 24;
        StringBuilder keyText = new StringBuilder(encryptTextPass);
        if (keyText.length() < maxLength) {
            while (keyText.length() < maxLength) {
                keyText.append("M");
            }
        } else {
            keyText = new StringBuilder(encryptTextPass.length() <= maxLength ? encryptTextPass : encryptTextPass.substring(0, maxLength));
        }
        byte[] key = Base64.getEncoder().encode(keyText.toString().getBytes());
        return new SecretKeySpec(key, ENCRYPTION_ALG);
    }

    protected boolean validateAction(UserEntity user, ModuleAction action) {
        if (user.getRole().getId().equals(IRoleService.SUPER_ADMIN_ID))
            return true;

        return user.getRole().getActions().stream().anyMatch(roleAction ->
                roleAction.getAction().getId().equals(action.getId())
        );
    }
}
