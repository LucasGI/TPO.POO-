package modelo;

public enum EstadoSalida {
    PROGRAMADA,
    CONFIRMADA,
    COMPLETA,
    EN_CURSO,
    FINALIZADA,
    CANCELADA;

    // transiciones permitidas segun RN02
    public boolean puedePasarA(EstadoSalida nuevo) {
        switch (this) {
            case PROGRAMADA:
                return nuevo == CONFIRMADA || nuevo == COMPLETA || nuevo == CANCELADA;
            case CONFIRMADA:
                return nuevo == PROGRAMADA || nuevo == COMPLETA || nuevo == EN_CURSO || nuevo == CANCELADA;
            case COMPLETA:
                return nuevo == CONFIRMADA || nuevo == PROGRAMADA || nuevo == EN_CURSO || nuevo == CANCELADA;
            case EN_CURSO:
                return nuevo == FINALIZADA;
            default:
                return false;
        }
    }

    public boolean admiteReservas() {
        return this == PROGRAMADA || this == CONFIRMADA;
    }
}
