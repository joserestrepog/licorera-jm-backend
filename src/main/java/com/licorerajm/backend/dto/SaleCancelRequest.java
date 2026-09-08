package com.licorerajm.backend.dto;

import jakarta.validation.constraints.Size;

public class SaleCancelRequest {

    @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}