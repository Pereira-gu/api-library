package com.gu_pereira.api_libary.usuario;

import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequestDTO(
        @NotNull(message = "A nova role não pode ser nula.")
        Role novaRole
) {}