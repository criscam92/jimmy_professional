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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author arturo
 */
@Entity
@Table(name = "pago_detalle", catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PagoDetalle.findAll", query = "SELECT p FROM PagoDetalle p"),
    @NamedQuery(name = "PagoDetalle.findById", query = "SELECT p FROM PagoDetalle p WHERE p.id = :id"),
    @NamedQuery(name = "PagoDetalle.findByValor", query = "SELECT p FROM PagoDetalle p WHERE p.valor = :valor"),
    @NamedQuery(name = "PagoDetalle.findByTipo", query = "SELECT p FROM PagoDetalle p WHERE p.tipo = :tipo")})
public class PagoDetalle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double valor;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int tipo;
    @JoinColumn(name = "pago", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Pago pago;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pagoDetalle", fetch = FetchType.LAZY)
    private List<PagoPublicidad> pagoPublicidadList;

    public PagoDetalle() {
    }

    public PagoDetalle(Long id) {
        this.id = id;
    }

    public PagoDetalle(Long id, double valor, int tipo) {
        this.id = id;
        this.valor = valor;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    @XmlTransient
    public List<PagoPublicidad> getPagoPublicidadList() {
        return pagoPublicidadList;
    }

    public void setPagoPublicidadList(List<PagoPublicidad> pagoPublicidadList) {
        this.pagoPublicidadList = pagoPublicidadList;
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
        if (!(object instanceof PagoDetalle)) {
            return false;
        }
        PagoDetalle other = (PagoDetalle) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.PagoDetalle[ id=" + id + " ]";
    }
    
}
