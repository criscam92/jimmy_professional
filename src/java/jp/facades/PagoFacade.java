/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.Pago;
import jp.util.EstadoPagoFactura;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class PagoFacade extends AbstractFacade<Pago> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagoFacade() {
        super(Pago.class);
    }

    public long countPagosFacturaByFactura(Factura factura) {
        try {
            Query query = getEntityManager().createQuery("SELECT COUNT(p) FROM Pago p WHERE p.factura.id = :fac AND p.estado = :est");
            query.setParameter("fac", factura.getId());
            query.setParameter("est", EstadoPagoFactura.ANULADO.getValor());
            return (long) query.getSingleResult();
        } catch (Exception e) {
        }
        return 0;
    }

    public List<Pago> getPagosByFactura(Factura factura) {
        List<Pago> pagos;
        try {
            Query query = getEntityManager().createQuery("SELECT p FROM Pago p WHERE p.factura.id = :fac AND p.estado = :est");
            query.setParameter("fac", factura.getId());
            query.setParameter("est", EstadoPagoFactura.REALIZADA.getValor());
            pagos = query.getResultList();
        } catch (Exception e) {
            pagos = new ArrayList<>();
        }
        return pagos;
    }

}
