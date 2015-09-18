package jp.entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "tipo_publicidad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoPublicidad.findAll", query = "SELECT t FROM TipoPublicidad t"),
    @NamedQuery(name = "TipoPublicidad.findById", query = "SELECT t FROM TipoPublicidad t WHERE t.id = :id"),
    @NamedQuery(name = "TipoPublicidad.findByDetalle", query = "SELECT t FROM TipoPublicidad t WHERE t.detalle = :detalle")})
public class TipoPublicidad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Size(max = 40)
    @Column(length = 40)
    private String detalle;
    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    private List<PagoPublicidad> pagoPublicidadList;

    public TipoPublicidad() {
    }

    public TipoPublicidad(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
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
        if (!(object instanceof TipoPublicidad)) {
            return false;
        }
        TipoPublicidad other = (TipoPublicidad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.TipoPublicidad[ id=" + id + " ]";
    }
    
}
