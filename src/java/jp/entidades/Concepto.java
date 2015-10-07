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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import jp.entidades.auxiliar.Codificable;

@Entity
@Table(name = "concepto", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})})
@NamedQueries({
    @NamedQuery(name = "Concepto.findAll", query = "SELECT c FROM Concepto c"),
    @NamedQuery(name = "Concepto.findById", query = "SELECT c FROM Concepto c WHERE c.id = :id"),
    @NamedQuery(name = "Concepto.findByDetalle", query = "SELECT c FROM Concepto c WHERE c.detalle = :detalle"),
    @NamedQuery(name = "Concepto.findByTipo", query = "SELECT c FROM Concepto c WHERE c.tipo = :tipo")})
public class Concepto implements Serializable, Codificable {
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
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Integer tipo;
    @NotNull
    @Size(min = 1, max = 45)
    @Column(nullable = false, length = 45)
    private String codigo;
    @NotNull
    @Column(nullable = false)
    private Integer cxccxp;
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

    @Override
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

    public Integer getTipo2() {
        return tipo;
    }

    public void setTipo2(Integer tipo) {
        this.tipo = tipo;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCxccxp() {
        return cxccxp;
    }

    public void setCxccxp(Integer cxccxp) {
        this.cxccxp = cxccxp;
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
        return this.getCodigo()+" - "+this.getDetalle();
    }

    @Override
    public String getTipo() {
        return null;
    }

    @Override
    public String getNombre() {
        return null;
    }
    
}
