package jp.entidades;

import javax.persistence.Id;

public class ProductoPromocionHelper {

    @Id
    private Long id;
    private Long idObject;
    private int unidadesVenta;
    private int unidadesBonificacion;
    private double precio;
    private double precioUs;
    private Object productoPromocion;
    private boolean isProducto;
    private boolean precioEditado;

    public ProductoPromocionHelper(Long id, Long idObject, int unidadesVenta, int unidadesBonificacion, double precio, double precioUs, Object productoPromocion, boolean isProducto, boolean precioEditado) {
        this.id = id;
        this.idObject = idObject;
        this.unidadesVenta = unidadesVenta;
        this.unidadesBonificacion = unidadesBonificacion;
        this.precio = precio;
        this.precioUs = precioUs;
        this.productoPromocion = productoPromocion;
        this.isProducto = isProducto;
        this.precioEditado = precioEditado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdObject() {
        return idObject;
    }

    public void setIdObject(Long idObject) {
        this.idObject = idObject;
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

    public boolean isPrecioEditado() {
        return precioEditado;
    }

    public void setPrecioEditado(boolean precioEditado) {
        this.precioEditado = precioEditado;
    }

}
