package jp.entidades;

public class CarteraFactura {

    private String factura;
    private double actual;
    private double dias30;
    private double mas30dias;
    private double mas60dias;
    private double mas90dias;

    public CarteraFactura() {
    }

    public CarteraFactura(String factura, double actual, double dias30, double mas30dias, double mas60dias, double mas90dias) {
        this.factura = factura;
        this.actual = actual;
        this.dias30 = dias30;
        this.mas30dias = mas30dias;
        this.mas60dias = mas60dias;
        this.mas90dias = mas90dias;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public double getActual() {
        return actual;
    }

    public void setActual(double actual) {
        this.actual = actual;
    }

    public double getDias30() {
        return dias30;
    }

    public void setDias30(double dias30) {
        this.dias30 = dias30;
    }

    public double getMas30dias() {
        return mas30dias;
    }

    public void setMas30dias(double mas30dias) {
        this.mas30dias = mas30dias;
    }

    public double getMas60dias() {
        return mas60dias;
    }

    public void setMas60dias(double mas60dias) {
        this.mas60dias = mas60dias;
    }

    public double getMas90dias() {
        return mas90dias;
    }

    public void setMas90dias(double mas90dias) {
        this.mas90dias = mas90dias;
    }

}