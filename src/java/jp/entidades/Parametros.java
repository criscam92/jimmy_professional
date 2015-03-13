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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author arturo
 */
@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
public class Parametros implements Serializable {
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_publicidad", nullable = false)
    private float porcentajePublicidad;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_comision", nullable = false)
    private float porcentajeComision;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_venta_public", nullable = false)
    private float porcentajeVentaPublic;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio_dolar", nullable = false)
    private float precioDolar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_pronto_pago", nullable = false)
    private float valorProntoPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_pronto_pago", nullable = false)
    private float porcentajeProntoPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dias_descuento_pronto_pago", nullable = false)
    private int diasDescuentoProntoPago;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String correo;
    @JoinColumn(name = "ciudad", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Ciudad ciudad;

    public Parametros() {
    }

    public Parametros(Integer id) {
        this.id = id;
    }

    public Parametros(Integer id, float recargoLocal, float recargoNacional, float recargoInternacional, float porcentajePublicidad, float porcentajeComision, float porcentajeVentaPublic, float precioDolar, float valorProntoPago, float porcentajeProntoPago, int diasDescuentoProntoPago, String correo) {
        this.id = id;
        this.recargoLocal = recargoLocal;
        this.recargoNacional = recargoNacional;
        this.recargoInternacional = recargoInternacional;
        this.porcentajePublicidad = porcentajePublicidad;
        this.porcentajeComision = porcentajeComision;
        this.porcentajeVentaPublic = porcentajeVentaPublic;
        this.precioDolar = precioDolar;
        this.valorProntoPago = valorProntoPago;
        this.porcentajeProntoPago = porcentajeProntoPago;
        this.diasDescuentoProntoPago = diasDescuentoProntoPago;
        this.correo = correo;
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

    public float getPorcentajeVentaPublic() {
        return porcentajeVentaPublic;
    }

    public void setPorcentajeVentaPublic(float porcentajeVentaPublic) {
        this.porcentajeVentaPublic = porcentajeVentaPublic;
    }

    public float getPrecioDolar() {
        return precioDolar;
    }

    public void setPrecioDolar(float precioDolar) {
        this.precioDolar = precioDolar;
    }

    public float getValorProntoPago() {
        return valorProntoPago;
    }

    public void setValorProntoPago(float valorProntoPago) {
        this.valorProntoPago = valorProntoPago;
    }

    public float getPorcentajeProntoPago() {
        return porcentajeProntoPago;
    }

    public void setPorcentajeProntoPago(float porcentajeProntoPago) {
        this.porcentajeProntoPago = porcentajeProntoPago;
    }

    public int getDiasDescuentoProntoPago() {
        return diasDescuentoProntoPago;
    }

    public void setDiasDescuentoProntoPago(int diasDescuentoProntoPago) {
        this.diasDescuentoProntoPago = diasDescuentoProntoPago;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
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
        if (!(object instanceof Parametros)) {
            return false;
        }
        Parametros other = (Parametros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Parametros[ id=" + id + " ]";
    }
    
}
