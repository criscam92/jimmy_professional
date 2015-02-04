/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author CRISTIAN
 */
@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p"),
    @NamedQuery(name = "Producto.findById", query = "SELECT p FROM Producto p WHERE p.id = :id"),
    @NamedQuery(name = "Producto.findByNombre", query = "SELECT p FROM Producto p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Producto.findByDescripcion", query = "SELECT p FROM Producto p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Producto.findByGramaje", query = "SELECT p FROM Producto p WHERE p.gramaje = :gramaje"),
    @NamedQuery(name = "Producto.findByValorCosto", query = "SELECT p FROM Producto p WHERE p.valorCosto = :valorCosto"),
    @NamedQuery(name = "Producto.findByValorVenta", query = "SELECT p FROM Producto p WHERE p.valorVenta = :valorVenta"),
    @NamedQuery(name = "Producto.findByValorVentaUsd", query = "SELECT p FROM Producto p WHERE p.valorVentaUsd = :valorVentaUsd")})
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String gramaje;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_costo", nullable = false)
    private double valorCosto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_venta", nullable = false)
    private double valorVenta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_venta_usd", precision = 17, scale = 17)
    private Double valorVentaUsd;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DevolucionProducto> devolucionProductoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<VisitaProducto> visitaProductoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<PromocionProducto> promocionProductoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<FacturaProducto> facturaProductoList;

    public Producto() {
    }

    public Producto(Long id) {
        this.id = id;
    }

    public Producto(Long id, String nombre, String descripcion, String gramaje, double valorCosto, double valorVenta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.gramaje = gramaje;
        this.valorCosto = valorCosto;
        this.valorVenta = valorVenta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getGramaje() {
        return gramaje;
    }

    public void setGramaje(String gramaje) {
        this.gramaje = gramaje;
    }

    public double getValorCosto() {
        return valorCosto;
    }

    public void setValorCosto(double valorCosto) {
        this.valorCosto = valorCosto;
    }

    public double getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(double valorVenta) {
        this.valorVenta = valorVenta;
    }

    public Double getValorVentaUsd() {
        return valorVentaUsd;
    }

    public void setValorVentaUsd(Double valorVentaUsd) {
        this.valorVentaUsd = valorVentaUsd;
    }

    @XmlTransient
    public List<DevolucionProducto> getDevolucionProductoList() {
        return devolucionProductoList;
    }

    public void setDevolucionProductoList(List<DevolucionProducto> devolucionProductoList) {
        this.devolucionProductoList = devolucionProductoList;
    }

    @XmlTransient
    public List<VisitaProducto> getVisitaProductoList() {
        return visitaProductoList;
    }

    public void setVisitaProductoList(List<VisitaProducto> visitaProductoList) {
        this.visitaProductoList = visitaProductoList;
    }

    @XmlTransient
    public List<PromocionProducto> getPromocionProductoList() {
        return promocionProductoList;
    }

    public void setPromocionProductoList(List<PromocionProducto> promocionProductoList) {
        this.promocionProductoList = promocionProductoList;
    }

    @XmlTransient
    public List<FacturaProducto> getFacturaProductoList() {
        return facturaProductoList;
    }

    public void setFacturaProductoList(List<FacturaProducto> facturaProductoList) {
        this.facturaProductoList = facturaProductoList;
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
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getGramaje()+" - "+this.getNombre();
    }
    
}
