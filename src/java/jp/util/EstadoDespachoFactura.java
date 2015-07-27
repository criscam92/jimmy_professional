package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum EstadoDespachoFactura {

    SIN_DESPACHAR(0, "Sin despachar"),
    DESPACHO_PARCIAL(1, "Despacho parcial"),
    DESPACHADO(2, "Despachado");

    private int valor;
    private String detalle;

    private EstadoDespachoFactura(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static EstadoDespachoFactura getFromValue(int value) {
        for (EstadoDespachoFactura tipo : EstadoDespachoFactura.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static EstadoDespachoFactura[] getFromValue(Integer[] arre) {
        EstadoDespachoFactura[] estado = new EstadoDespachoFactura[arre.length];
        int count = 0;
        for (int b : arre) {
            estado[count] = getFromValue(b);
            count++;
        }
        return estado;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (EstadoDespachoFactura estado : EstadoDespachoFactura.values()) {
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
