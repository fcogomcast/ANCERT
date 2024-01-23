package es.stpa.plusvalias.domain.intercambio;


public enum Duplicado {

    D,
    T,
    C;

    public String value() {
        return name();
    }

    public static Duplicado fromValue(String v) {
        return valueOf(v);
    }

}