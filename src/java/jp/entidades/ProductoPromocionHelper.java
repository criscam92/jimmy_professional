package jp.entidades;

public class ProductoPromocionHelper{
    private int unidadesVenta;
    private int unidadesBonificacion;
    private double precio;
    private Object productoPromocion;

    public ProductoPromocionHelper() {
        
    }

    
    public ProductoPromocionHelper(int unidadesVenta, int unidadesBonificacion, double precio, Object productoPromocion) {
        this.unidadesVenta = unidadesVenta;
        this.unidadesBonificacion = unidadesBonificacion;
        this.precio = precio;
        this.productoPromocion = productoPromocion;
    }

    public int getUnidadesVenta() {
        return unidadesVenta;
    }

    public void setUnidadesVenta(int unidadesVenta) {
        this.unidadesVenta = unidadesVenta;
    }

    public int getUnidadesBonificacion() {
        return unidadesBonificacion;
    }

    public void setUnidadesBonificacion(int unidadesBonificacion) {
        this.unidadesBonificacion = unidadesBonificacion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Object getProductoPromocion() {
        return productoPromocion;
    }

    public void setProductoPromocion(Object productoPromocion) {
        this.productoPromocion = productoPromocion;
    }
    
}