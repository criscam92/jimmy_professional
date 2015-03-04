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
@Table(name = "factura_promocion", catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FacturaPromocion.findAll", query = "SELECT f FROM FacturaPromocion f"),
    @NamedQuery(name = "FacturaPromocion.findById", query = "SELECT f FROM FacturaPromocion f WHERE f.id = :id"),
    @NamedQuery(name = "FacturaPromocion.findByUnidadesVenta", query = "SELECT f FROM FacturaPromocion f WHERE f.unidadesVenta = :unidadesVenta"),
    @NamedQuery(name = "FacturaPromocion.findByUnidadesBonifciacion", query = "SELECT f FROM FacturaPromocion f WHERE f.unidadesBonifciacion = :unidadesBonifciacion"),
    @NamedQuery(name = "FacturaPromocion.findByPrecio", query = "SELECT f FROM FacturaPromocion f WHERE f.precio = :precio")})
public class FacturaPromocion implements Serializable {
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
    @Column(name = "unidades_bonifciacion", nullable = false)
    private int unidadesBonificiacion;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double precio;
    @JoinColumn(name = "factura", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Factura factura;
    @JoinColumn(name = "promocion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Promocion promocion;

    public FacturaPromocion() {
    }

    public FacturaPromocion(Long id) {
        this.id = id;
    }

    public FacturaPromocion(Long id, int unidadesVenta, int unidadesBonifciacion, double precio) {
        this.id = id;
        this.unidadesVenta = unidadesVenta;
        this.unidadesBonificiacion = unidadesBonifciacion;
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
        return unidadesBonificiacion;
    }

    public void setUnidadesBonificacion(int unidadesBonifciacion) {
        this.unidadesBonificiacion = unidadesBonifciacion;
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

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
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
        if (!(object instanceof FacturaPromocion)) {
            return false;
        }
        FacturaPromocion other = (FacturaPromocion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.FacturaPromocion[ id=" + id + " ]";
    }
    
}
