package com.example.labreservation.common;

public record Result<T>(boolean success, String message, T data) {
    public static <T> Result<T> ok(T data) {
        return new Result<>(true, "OK", data);
    }

    public static Result<Void> ok() {
        return new Result<>(true, "OK", null);
    }

    public static Result<Void> fail(String message) {
        return new Result<>(false, message, null);
    }
}
