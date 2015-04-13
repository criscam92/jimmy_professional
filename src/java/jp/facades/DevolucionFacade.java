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

    public List<Factura> getFacturasPendientesByCliente(Cliente c, boolean monedaDolar) {
        List<Factura> facturasPendientesTMP;
        try {
            Query queryFactura = em.createQuery("SELECT f FROM Factura f WHERE f.cliente.id= :clie AND f.estado<> :est1 AND f.estado<> :est2 AND f.dolar = :dol");
            queryFactura.setParameter("clie", c.getId());
            queryFactura.setParameter("est1", EstadoPagoFactura.ANULADO.getValor());
            queryFactura.setParameter("est2", EstadoPagoFactura.CANCELADO.getValor());
            queryFactura.setParameter("dol", monedaDolar);
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
                    f.setSaldoCancelado(0d);
                    facturasFinal.add(f);
                    continue;
                } else {
                    if (totalPago < totalFactura) {
                        f.setSaldo(totalFactura - totalPago);
                        f.setSaldoCancelado(totalPago);
                        facturasFinal.add(f);
                    }
                }

            }

            for (Factura ff : facturasFinal) {
                System.out.println("factura a devolver-> " + ff.getObservaciones());
            }
            return facturasFinal;

        } catch (NoResultException nre) {
            return null;
        }
    }

}
