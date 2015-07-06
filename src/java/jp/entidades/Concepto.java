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

@Entity
@Table(name = "concepto")
@NamedQueries({
    @NamedQuery(name = "Concepto.findAll", query = "SELECT c FROM Concepto c"),
    @NamedQuery(name = "Concepto.findById", query = "SELECT c FROM Concepto c WHERE c.id = :id"),
    @NamedQuery(name = "Concepto.findByDetalle", query = "SELECT c FROM Concepto c WHERE c.detalle = :detalle"),
    @NamedQuery(name = "Concepto.findByBoolean1", query = "SELECT c FROM Concepto c WHERE c.boolean1 = :boolean1")})
public class Concepto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String detalle;
    @Column(name = "boolean")
    private Serializable boolean1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "concepto", fetch = FetchType.LAZY)
    private List<ReciboCaja> reciboCajaList;

    public Concepto() {
    }

    public Concepto(Integer id) {
        this.id = id;
    }

    public Concepto(Integer id, String detalle) {
        this.id = id;
        this.detalle = detalle;
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

    public Serializable getBoolean1() {
        return boolean1;
    }

    public void setBoolean1(Serializable boolean1) {
        this.boolean1 = boolean1;
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
        if (!(object instanceof Concepto)) {
            return false;
        }
        Concepto other = (Concepto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.entidades.Concepto[ id=" + id + " ]";
    }
    
}
