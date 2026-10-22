package com.ecoalerta.backend.dto;

import java.util.Map;

public record ApiError(int status, String mensaje, Map<String, String> errores) { }
