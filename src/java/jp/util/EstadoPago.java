package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum EstadoPago {

    REALIZADO(0, "Realizado"),
    ANULADO(1, "Anulado");

    private int valor;
    private String detalle;

    private EstadoPago(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static EstadoPago getFromValue(int value) {
        for (EstadoPago tipo : EstadoPago.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static EstadoPago[] getFromValue(Integer[] arre) {
        EstadoPago[] estado = new EstadoPago[arre.length];
        int count = 0;
        for (int b : arre) {
            estado[count] = getFromValue(b);
            count++;
        }
        return estado;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (EstadoPago estado : EstadoPago.values()) {
            mapTMP.put(estado.getValor(), estado.getDetalle());

        }
        return mapTMP;
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
