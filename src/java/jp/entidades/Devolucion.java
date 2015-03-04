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
 * @author arturo
 */
@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Devolucion.findAll", query = "SELECT d FROM Devolucion d"),
    @NamedQuery(name = "Devolucion.findById", query = "SELECT d FROM Devolucion d WHERE d.id = :id"),
    @NamedQuery(name = "Devolucion.findByDolar", query = "SELECT d FROM Devolucion d WHERE d.dolar = :dolar"),
    @NamedQuery(name = "Devolucion.findByValorTotal", query = "SELECT d FROM Devolucion d WHERE d.valorTotal = :valorTotal"),
    @NamedQuery(name = "Devolucion.findByObservaciones", query = "SELECT d FROM Devolucion d WHERE d.observaciones = :observaciones")})
public class Devolucion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean dolar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_total", nullable = false)
    private float valorTotal;
    @Size(max = 300)
    @Column(length = 300)
    private String observaciones;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "devolucion", fetch = FetchType.LAZY)
    private List<CambioDevolucion> cambioDevolucionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "devolucion", fetch = FetchType.LAZY)
    private List<DevolucionProducto> devolucionProductoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "devolucion", fetch = FetchType.LAZY)
    private List<PagoDevolucion> pagoDevolucionList;

    public Devolucion() {
    }

    public Devolucion(Long id) {
        this.id = id;
    }

    public Devolucion(Long id, boolean dolar, float valorTotal) {
        this.id = id;
        this.dolar = dolar;
        this.valorTotal = valorTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getDolar() {
        return dolar;
    }

    public void setDolar(boolean dolar) {
        this.dolar = dolar;
    }

    public float getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    public List<CambioDevolucion> getCambioDevolucionList() {
        return cambioDevolucionList;
    }

    public void setCambioDevolucionList(List<CambioDevolucion> cambioDevolucionList) {
        this.cambioDevolucionList = cambioDevolucionList;
    }

    @XmlTransient
    public List<DevolucionProducto> getDevolucionProductoList() {
        return devolucionProductoList;
    }

    public void setDevolucionProductoList(List<DevolucionProducto> devolucionProductoList) {
        this.devolucionProductoList = devolucionProductoList;
    }

    @XmlTransient
    public List<PagoDevolucion> getPagoDevolucionList() {
        return pagoDevolucionList;
    }

    public void setPagoDevolucionList(List<PagoDevolucion> pagoDevolucionList) {
        this.pagoDevolucionList = pagoDevolucionList;
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
        if (!(object instanceof Devolucion)) {
            return false;
        }
        Devolucion other = (Devolucion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Devolucion[ id=" + id + " ]";
    }
    
}
