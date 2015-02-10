package jp.util;

import java.util.Map;
import java.util.TreeMap;

public enum TipoUsuario {

    NORMAL(0, "Normal"),
    Administrador(1, "Administrador");

    private int valor;
    private String detalle;

    private TipoUsuario(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static TipoUsuario getFromValue(int value) {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static Map<String, Integer> getMapTipoUsuarios() {
        TreeMap<String, Integer> mapa = new TreeMap<>();
        for (TipoUsuario tipoUsuario : TipoUsuario.values()) {
            mapa.put(tipoUsuario.getDetalle(), tipoUsuario.getValor());
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
