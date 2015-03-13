package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum TipoCuentaBancaria {

    AHORROS(0, "Ahorros"),
    CORRIENTE(1, "Corriente");

    private int valor;
    private String detalle;

    private TipoCuentaBancaria(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static TipoCuentaBancaria getFromValue(int value) {
        for (TipoCuentaBancaria tipoCuenta : TipoCuentaBancaria.values()) {
            if (tipoCuenta.getValor() == value) {
                return tipoCuenta;
            }
        }
        return null;
    }

    public static TipoCuentaBancaria[] getFromValue(Integer[] arre) {
        TipoCuentaBancaria[] tiposCuenta = new TipoCuentaBancaria[arre.length];
        int count = 0;
        for (int b : arre) {
            tiposCuenta[count] = getFromValue(b);
            count++;
        }
        System.out.println("Size-> "+tiposCuenta.length);
        return tiposCuenta;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (TipoCuentaBancaria tipoCuenta : TipoCuentaBancaria.values()) {
            mapTMP.put(tipoCuenta.getValor(), tipoCuenta.getDetalle());

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
