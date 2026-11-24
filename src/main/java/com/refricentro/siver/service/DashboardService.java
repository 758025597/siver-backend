package com.refricentro.siver.service;

import com.refricentro.siver.dto.DashboardResponse;

public interface DashboardService {

    /** Indicadores del dia y del mes, filtrados segun el rol de quien los pide. */
    DashboardResponse resumen(String rol);
}
