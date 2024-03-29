package es.stpa.plusvalias.domain.intercambio;

public enum TipoVia {

    AC,
    AG,
    AL,
    AN,
    AR,
    AU,
    AV,
    AY,
    BJ,
    BL,
    BO,
    BQ,
    BR,
    CA,
    CG,
    CH,
    CI,
    CJ,
    CL,
    CM,
    CN,
    CO,
    CP,
    CR,
    CS,
    CT,
    CU,
    CY,
    CZ,
    DE,
    DP,
    DS,
    ED,
    EM,
    EN,
    EP,
    ER,
    ES,
    EX,
    FC,
    FN,
    GL,
    GR,
    GV,
    HT,
    JR,
    LD,
    LA,
    LG,
    MA,
    MC,
    ML,
    MN,
    MS,
    MT,
    MZ,
    PB,
    PC,
    PD,
    PI,
    PJ,
    PL,
    PM,
    PQ,
    PR,
    PS,
    PT,
    PU,
    PZ,
    QT,
    RA,
    RB,
    RC,
    RD,
    RM,
    RP,
    RR,
    RU,
    SA,
    SC,
    SD,
    SL,
    SN,
    SU,
    TN,
    TO,
    TR,
    UR,
    VA,
    VD,
    VI,
    VL,
    VR;

    public String value() {
        return name();
    }

    public static TipoVia fromValue(String v) {
        return valueOf(v);
    }

}
