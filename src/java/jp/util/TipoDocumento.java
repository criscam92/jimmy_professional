package jp.util;

import java.util.Map;
import java.util.TreeMap;

public enum TipoDocumento {

    CEDULA(0, "Cedula"),
    PASAPORTE(1, "Pasaporte"),
    REGISTRO_CIVIL(2, "Registro Civil"),
    NIT(3, "NIT");

    private int valor;
    private String detalle;

    private TipoDocumento(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static TipoDocumento getFromValue(int value) {
        for (TipoDocumento tipo : TipoDocumento.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static Map<String, Integer> getMapTipoDocumentos() {
        TreeMap<String, Integer> mapa = new TreeMap<>();
        for (TipoDocumento tipoDocumento : TipoDocumento.values()) {
            mapa.put(tipoDocumento.getDetalle(), tipoDocumento.getValor());
        }
        return mapa;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return detalle;
    }

}
