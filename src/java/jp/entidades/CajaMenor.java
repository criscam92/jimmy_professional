package jp.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "caja_menor")
@NamedQueries({
    @NamedQuery(name = "CajaMenor.findAll", query = "SELECT c FROM CajaMenor c"),
    @NamedQuery(name = "CajaMenor.findById", query = "SELECT c FROM CajaMenor c WHERE c.id = :id"),
    @NamedQuery(name = "CajaMenor.findByDetalle", query = "SELECT c FROM CajaMenor c WHERE c.detalle = :detalle"),
    @NamedQuery(name = "CajaMenor.findByBase", query = "SELECT c FROM CajaMenor c WHERE c.base = :base"),
    @NamedQuery(name = "CajaMenor.findByFechaActualizacion", query = "SELECT c FROM CajaMenor c WHERE c.fechaActualizacion = :fechaActualizacion")})
public class CajaMenor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String detalle;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long base;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "caja", fetch = FetchType.LAZY)
    private List<ActualizaBaseCajaMenor> actualizaBaseCajaMenorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "caja", fetch = FetchType.LAZY)
    private List<ReciboCaja> reciboCajaList;

    public CajaMenor() {
    }

    public CajaMenor(Integer id) {
        this.id = id;
    }

    public CajaMenor(Integer id, String detalle, long base, Date fechaActualizacion) {
        this.id = id;
        this.detalle = detalle;
        this.base = base;
        this.fechaActualizacion = fechaActualizacion;
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

    public long getBase() {
        return base;
    }

    public void setBase(long base) {
        this.base = base;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<ActualizaBaseCajaMenor> getActualizaBaseCajaMenorList() {
        return actualizaBaseCajaMenorList;
    }

    public void setActualizaBaseCajaMenorList(List<ActualizaBaseCajaMenor> actualizaBaseCajaMenorList) {
        this.actualizaBaseCajaMenorList = actualizaBaseCajaMenorList;
    }

    public List<ReciboCaja> getReciboCajaList() {
        return reciboCajaList;
    }

    public void setReciboCajaList(List<ReciboCaja> reciboCajaList) {
        this.reciboCajaList = reciboCajaList;
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
        if (!(object instanceof CajaMenor)) {
            return false;
        }
        CajaMenor other = (CajaMenor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.entidades.CajaMenor[ id=" + id + " ]";
    }
    
}
