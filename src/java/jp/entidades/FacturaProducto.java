/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author arturo
 */
@Entity
@Table(name = "factura_producto", catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FacturaProducto.findAll", query = "SELECT f FROM FacturaProducto f"),
    @NamedQuery(name = "FacturaProducto.findById", query = "SELECT f FROM FacturaProducto f WHERE f.id = :id"),
    @NamedQuery(name = "FacturaProducto.findByUnidadesVenta", query = "SELECT f FROM FacturaProducto f WHERE f.unidadesVenta = :unidadesVenta"),
    @NamedQuery(name = "FacturaProducto.findByUnidadesBonificacion", query = "SELECT f FROM FacturaProducto f WHERE f.unidadesBonificacion = :unidadesBonificacion"),
    @NamedQuery(name = "FacturaProducto.findByPrecio", query = "SELECT f FROM FacturaProducto f WHERE f.precio = :precio")})
public class FacturaProducto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "unidades_venta", nullable = false)
    private int unidadesVenta;
    @Basic(optional = false)
    @NotNull
    @Column(name = "unidades_bonificacion", nullable = false)
    private int unidadesBonificacion;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double precio;
    @JoinColumn(name = "factura", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Factura factura;
    @JoinColumn(name = "producto", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;

    public FacturaProducto() {
    }

    public FacturaProducto(Long id) {
        this.id = id;
    }

    public FacturaProducto(Long id, int unidadesVenta, int unidadesBonificacion, double precio) {
        this.id = id;
        this.unidadesVenta = unidadesVenta;
        this.unidadesBonificacion = unidadesBonificacion;
        this.precio = precio;
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

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FacturaProducto)) {
            return false;
        }
        FacturaProducto other = (FacturaProducto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.FacturaProducto[ id=" + id + " ]";
    }
    
}
