package com.gigatownpartners.dashboard.utils;

public final class PasswordValidator {
    private PasswordValidator() {}

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }

        return password.matches(PASSWORD_PATTERN);
    }
}
