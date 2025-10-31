package ar.edu.unq.futapp.exception

import java.lang.RuntimeException

class OverloadBrowserException: RuntimeException() {
    override val message: String
        get() = "En este momento hay muchos usuarios utilizando el servicio, por favor intente nuevamente en unos minutos."
}