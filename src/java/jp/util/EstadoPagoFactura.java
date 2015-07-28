package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum EstadoPagoFactura {

    SIN_PAGO(0, "Sin pago"),
    PAGO_PARCIAL(1, "Pago parcial"),
    PAGADA(2, "Pagada"),
    ANULADO(3, "Anulado");

    private int valor;
    private String detalle;

    private EstadoPagoFactura(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static EstadoPagoFactura getFromValue(int value) {
        for (EstadoPagoFactura tipo : EstadoPagoFactura.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static EstadoPagoFactura[] getFromValue(Integer[] arre) {
        EstadoPagoFactura[] estado = new EstadoPagoFactura[arre.length];
        int count = 0;
        for (int b : arre) {
            estado[count] = getFromValue(b);
            count++;
        }
        return estado;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (EstadoPagoFactura estado : EstadoPagoFactura.values()) {
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