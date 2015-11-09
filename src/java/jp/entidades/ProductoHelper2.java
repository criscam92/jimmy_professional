package jp.entidades;

public class ProductoHelper2 {

    private long id;
    private Producto producto;
    private int cantBonificacion;
    private double valBonificacion;
    private int cantVenta;
    private double valVenta;

    public ProductoHelper2() {
    }

    public ProductoHelper2(long id, Producto producto, int cantBonificacion, double valBonificacion, int cantVenta, double valVenta) {
        this.id = id;
        this.producto = producto;
        this.cantBonificacion = cantBonificacion;
        this.valBonificacion = valBonificacion;
        this.cantVenta = cantVenta;
        this.valVenta = valVenta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantBonificacion() {
        return cantBonificacion;
    }

    public void setCantBonificacion(int cantBonificacion) {
        this.cantBonificacion = cantBonificacion;
    }

    public double getValBonificacion() {
        return valBonificacion;
    }

    public void setValBonificacion(double valBonificacion) {
        this.valBonificacion = valBonificacion;
    }

    public int getCantVenta() {
        return cantVenta;
    }

    public void setCantVenta(int cantVenta) {
        this.cantVenta = cantVenta;
    }

    public double getValVenta() {
        return valVenta;
    }

    public void setValVenta(double valVenta) {
        this.valVenta = valVenta;
    }
}
