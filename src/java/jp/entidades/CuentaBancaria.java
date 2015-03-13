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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import jp.entidades.auxiliar.Codificable;

@Entity
@Table(name = "cuenta_bancaria", catalog = "jimmy_professional", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CuentaBancaria.findAll", query = "SELECT c FROM CuentaBancaria c"),
    @NamedQuery(name = "CuentaBancaria.findById", query = "SELECT c FROM CuentaBancaria c WHERE c.id = :id"),
    @NamedQuery(name = "CuentaBancaria.findByNombre", query = "SELECT c FROM CuentaBancaria c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "CuentaBancaria.findByNumeroCuenta", query = "SELECT c FROM CuentaBancaria c WHERE c.numeroCuenta = :numeroCuenta"),
    @NamedQuery(name = "CuentaBancaria.findByTipoCuenta", query = "SELECT c FROM CuentaBancaria c WHERE c.tipoCuenta = :tipoCuenta")})
public class CuentaBancaria implements Serializable, Codificable {
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
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "numero_cuenta", nullable = false, length = 20)
    private String numeroCuenta;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tipo_cuenta", nullable = false)
    private int tipoCuenta;
    @OneToMany(mappedBy = "cuenta", fetch = FetchType.LAZY)
    private List<Pago> pagoList;

    public CuentaBancaria() {
    }

    public CuentaBancaria(Integer id) {
        this.id = id;
    }

    public CuentaBancaria(Integer id, String nombre, String numeroCuenta, int tipoCuenta) {
        this.id = id;
        this.nombre = nombre;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public int getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(int tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    @XmlTransient
    public List<Pago> getPagoList() {
        return pagoList;
    }

    public void setPagoList(List<Pago> pagoList) {
        this.pagoList = pagoList;
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
        if (!(object instanceof CuentaBancaria)) {
            return false;
        }
        CuentaBancaria other = (CuentaBancaria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getNombre()+", "+this.getNumeroCuenta();
    }

    @Override
    public String getCodigo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTipo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
