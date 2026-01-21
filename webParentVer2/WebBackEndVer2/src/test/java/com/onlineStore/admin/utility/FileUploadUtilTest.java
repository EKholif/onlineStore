package com.onlineStore.admin.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileUploadUtilTest {

    @Test
    public void testValidatePath_ProhibitedPath() {
        // Reflection or just calling the method if it was public.
        // Since validatePath is private, I have to test via saveFile or cleanDir.
        // But saveFile requires MultipartFile. cleanDir is easier as it takes String.
        // Wait, cleanDir attempts to list files. It might fail with IOException if dir
        // doesn't exist,
        // but validatePath is called FIRST.

        String prohibited = "site-logo/1";

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            FileUploadUtil.cleanDir(prohibited);
        });

        Assertions.assertTrue(exception.getMessage().contains("Architecture Violation"));
    }

    @Test
    public void testValidatePath_ValidPath() {
        // This should fail with IOException (invalid directory) NOT
        // IllegalArgumentException (policy)
        // or effectively pass the validation check.
        String valid = "tenants/1/assets/users";

        try {
            FileUploadUtil.cleanDir(valid);
        } catch (Exception e) {
            // It might throw IOException because path doesn't exist, but that means it
            // passed validation.
            // If it threw IllegalArgumentException, it failed validation.
            Assertions.assertFalse(e instanceof IllegalArgumentException,
                    "Should not throw IllegalArgumentException for valid path");
        }
    }
}
