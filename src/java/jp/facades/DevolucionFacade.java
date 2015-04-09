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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Cliente;
import jp.entidades.Devolucion;
import jp.entidades.Factura;
import jp.entidades.Pago;
import jp.util.EstadoPagoFactura;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class DevolucionFacade extends AbstractFacade<Devolucion> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DevolucionFacade() {
        super(Devolucion.class);
    }

    public Long getCantidadProductoByDevolucion(Devolucion d) {
        try {
            Query q = getEntityManager().createQuery("SELECT SUM(dp.cantidad) FROM DevolucionProducto dp WHERE dp.devolucion.id= :dev");
            q.setParameter("dev", d.getId());
            Long cantidad = (Long) q.getSingleResult();
            return cantidad != null ? cantidad : null;
        } catch (Exception e) {
            System.out.println("NO SE PUDO CONSULTAR EL NUMERO DE PRODBYDEVOLUCION");
            e.printStackTrace();
            return null;
        }
    }

    public List<Factura> getFacturasPendientesByCliente(Cliente c) {
        List<Factura> facturasPendientesTMP;
        try {
            Query queryFactura = em.createQuery("SELECT f FROM Factura f WHERE f.cliente.id= :clie AND f.estado<> :est1 AND f.estado<> :est2");
            queryFactura.setParameter("clie", c.getId());
            queryFactura.setParameter("est1", EstadoPagoFactura.ANULADO.getValor());
            queryFactura.setParameter("est2", EstadoPagoFactura.CANCELADO.getValor());
            facturasPendientesTMP = queryFactura.getResultList();

            return getFacturasPendientesPago(facturasPendientesTMP);

//            return facturasPendientesTMP;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Factura> getFacturasPendientesPago(List<Factura> fs) {
        List<Factura> facturasFinal = new ArrayList<>();

        try {
            for (Factura f : fs) {
                Query q = em.createQuery("SELECT SUM(p.valorTotal) FROM Pago p WHERE p.factura.id = :f");
                q.setParameter("f", f.getId());
                Double totalPago = (Double) q.getSingleResult();
                System.out.println("totalPago de factura: " + f + "->" + totalPago);

                Query q2 = em.createQuery("SELECT SUM(f.totalPagar) FROM Factura f WHERE f.id = :f");
                q2.setParameter("f", f.getId());
                Double totalFactura = (Double) q2.getSingleResult();
                System.out.println("totalFactura de factura: " + f + "->" + totalFactura);

                if (totalPago == null) {
                    f.setSaldo(f.getTotalPagar());
                    facturasFinal.add(f);
                    continue;
                } else {
                    if (totalPago < totalFactura) {
                        f.setSaldo(totalFactura - totalPago);
                        facturasFinal.add(f);
                    }
                }

            }

            /*Query queryPagos = em.createQuery("SELECT p FROM Pago p WHERE p.factura IN :facts");
             queryPagos.setParameter("facts", fs);
             pagosFactura = queryPagos.getResultList();

             Query queryFacturas = em.createQuery("SELECT p.factura FROM Pago p WHERE p.factura IN :facts");
             queryFacturas.setParameter("facts", fs);
             facturas = queryFacturas.getResultList();

             for (Pago pago : pagosFactura) {
             for (Factura factura : facturas) {
             if (factura == pago.getFactura() && factura.getTotalPagar() > pago.getValorTotal()) {
             facturasFinal.add(factura);
             }
             }
             }*/
            for (Factura ff : facturasFinal) {
                System.out.println("factura a devolver-> " + ff.getObservaciones());
            }
            return facturasFinal;

        } catch (NoResultException nre) {
            return null;
        }
    }

}
