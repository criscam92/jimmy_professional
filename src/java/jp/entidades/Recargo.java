package jp.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Recargo.findAll", query = "SELECT r FROM Recargo r"),
    @NamedQuery(name = "Recargo.findById", query = "SELECT r FROM Recargo r WHERE r.id = :id"),
    @NamedQuery(name = "Recargo.findByRecargoLocal", query = "SELECT r FROM Recargo r WHERE r.recargoLocal = :recargoLocal"),
    @NamedQuery(name = "Recargo.findByRecargoNacional", query = "SELECT r FROM Recargo r WHERE r.recargoNacional = :recargoNacional"),
    @NamedQuery(name = "Recargo.findByRecargoInternacional", query = "SELECT r FROM Recargo r WHERE r.recargoInternacional = :recargoInternacional"),
    @NamedQuery(name = "Recargo.findByCiudad", query = "SELECT r FROM Recargo r WHERE r.ciudad = :ciudad")})
public class Recargo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recargo_local", nullable = false)
    private float recargoLocal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recargo_nacional", nullable = false)
    private float recargoNacional;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recargo_internacional", nullable = false)
    private float recargoInternacional;
    @Basic(optional = true)
    @NotNull
    @Column(name = "porcentaje_publicidad", nullable = false)
    private float porcentajePublicidad;
    @Basic(optional = true)
    @NotNull
    @Column(name = "porcentaje_comision", nullable = false)
    private float porcentajeComision;
    @JoinColumn(name = "ciudad", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Ciudad ciudad;

    public Recargo() {
    }

    public Recargo(Integer id) {
        this.id = id;
    }

    public Recargo(Integer id, float recargoLocal, float recargoNacional, float recargoInternacional, float porcentajePublicidad, float porcentajeComision, Ciudad ciudad) {
        this.id = id;
        this.recargoLocal = recargoLocal;
        this.recargoNacional = recargoNacional;
        this.recargoInternacional = recargoInternacional;
        this.porcentajeComision = porcentajeComision;
        this.porcentajePublicidad = porcentajePublicidad;
        this.ciudad = ciudad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getRecargoLocal() {
        return recargoLocal;
    }

    public void setRecargoLocal(float recargoLocal) {
        this.recargoLocal = recargoLocal;
    }

    public float getRecargoNacional() {
        return recargoNacional;
    }

    public void setRecargoNacional(float recargoNacional) {
        this.recargoNacional = recargoNacional;
    }

    public float getRecargoInternacional() {
        return recargoInternacional;
    }

    public void setRecargoInternacional(float recargoInternacional) {
        this.recargoInternacional = recargoInternacional;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public float getPorcentajePublicidad() {
        return porcentajePublicidad;
    }

    public void setPorcentajePublicidad(float porcentajePublicidad) {
        this.porcentajePublicidad = porcentajePublicidad;
    }

    public float getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(float porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
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
        if (!(object instanceof Recargo)) {
            return false;
        }
        Recargo other = (Recargo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.entidades.Recargo[ id=" + id + " ]";
    }
    
}
