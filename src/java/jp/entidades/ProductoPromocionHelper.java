package jp.entidades;

import javax.persistence.Id;

public class ProductoPromocionHelper {

    @Id
    private Long id;
    private int unidadesVenta;
    private int unidadesBonificacion;
    private double precio;
    private double precioUs;
    private Object productoPromocion;
    private boolean isProducto;

    public ProductoPromocionHelper(Long id, int unidadesVenta, int unidadesBonificacion, double precio, double precioUs, Object productoPromocion, boolean isProducto) {
        this.id = id;
        this.unidadesVenta = unidadesVenta;
        this.unidadesBonificacion = unidadesBonificacion;
        this.precio = precio;
        this.precioUs = precioUs;
        this.productoPromocion = productoPromocion;
        this.isProducto = isProducto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getPrecioUs() {
        return precioUs;
    }

    public void setPrecioUs(double precioUs) {
        this.precioUs = precioUs;
    }

    public Object getProductoPromocion() {
        return productoPromocion;
    }

    public void setProductoPromocion(Object productoPromocion) {
        this.productoPromocion = productoPromocion;
    }

    public boolean isProducto() {
        return isProducto;
    }

    public void setProducto(boolean isProducto) {
        this.isProducto = isProducto;
    }
}
