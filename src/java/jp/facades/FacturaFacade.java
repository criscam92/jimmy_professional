package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jp.entidades.Cliente;
import jp.entidades.DespachoFactura;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.Pago;
import jp.entidades.Promocion;
import jp.util.EstadoPagoFactura;
import jp.util.Moneda;

@Stateless
public class FacturaFacade extends AbstractFacade<Factura> {
    
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaFacade() {
        super(Factura.class);
    }

    public Factura findFacturaByOrdenPedido(String ordenPedido) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Factura> fac = cq.from(Factura.class);
        cq.select(fac);
        cq.where(cb.equal(fac.get("ordenPedido"), ordenPedido));
        TypedQuery<Factura> q = getEntityManager().createQuery(cq);
        q.setFirstResult(1);
        return q.getSingleResult();
    }

    public double getValorPendientePagoFactura(Factura factura) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();

            Root<Pago> fac = cq.from(Pago.class);
            cq.select(cb.sum(fac.get("valorTotal").as(Double.class)));
            cq.where(cb.and(cb.equal(fac.get("factura"), factura),
                    cb.equal(fac.get("estado"), EstadoPagoFactura.REALIZADA.getValor())));
            TypedQuery<Double> q = getEntityManager().createQuery(cq);
            Double valorAbono = q.getSingleResult();

            if (valorAbono == null) {
                valorAbono = 0.0d;
            }

            System.out.println("Valor Factura: ".concat(factura.getTotalPagar() + ""));
            System.out.println("Valor Abono: ".concat(valorAbono + ""));
            return factura.getTotalPagar() - valorAbono;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public List<Promocion> getPromocionByQuery(String query) {
        try {
            List<Promocion> promociones;
            Query queryPromocion = getEntityManager().createQuery("SELECT p FROM Promocion p WHERE UPPER(CONCAT(p.codigo,' - ',p.descripcion)) LIKE UPPER(:param)");
            queryPromocion.setParameter("param", "%" + query + "%");
            promociones = queryPromocion.getResultList();

            queryPromocion.setFirstResult(0);
            queryPromocion.setMaxResults(10);

            return promociones;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Factura getFacturaById(Long idFactura) {
        try {
            return getEntityManager().find(Factura.class, idFactura);
        } catch (NoResultException e) {
        }
        return null;
    }

    public Factura getFacturaByOrdenPedido(String ordenPedido) {
        try {
            Query query = getEntityManager().createQuery("SELECT f FROM Factura f WHERE f.ordenPedido = :op");
            query.setParameter("op", ordenPedido);
            query.setMaxResults(1);
            return (Factura) query.getSingleResult();
        } catch (NoResultException e) {
        }
        return null;
    }

    public List<DespachoFactura> getDespachosFacturaByFactura(Factura factura) {
        try {
            Query query = getEntityManager().createQuery("SELECT fp FROM DespachoFactura fp WHERE fp.factura.id = :fac");
            query.setParameter("fac", factura.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    public List<FacturaProducto> getFacturaProductoByFactura(Factura factura) {
        try {
            Query query = getEntityManager().createQuery("SELECT fp FROM FacturaProducto fp WHERE fp.factura.id = :fac");
            query.setParameter("fac", factura.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }
   
    public List<Factura> getFacturasPendientesByCliente(Cliente c){
        return getFacturasPendientesByCliente(c, null);
    }
    
    /**
     * Retorna las facturas con pago pendiente del cliente indicado y con la moneda indicada
     * @param c Instancia del cliente de las facturas
     * @param moneda Indica la moneda a buscar, si desea obtener todas las facturas, envíe el valor nulo
     * @return listado de facturas, si no encuentra, retorna nulo
     * 
     * @see Moneda
     * 
     */
    public List<Factura> getFacturasPendientesByCliente(Cliente c, Moneda moneda) {
        List<Factura> facturasPendientesTMP;
        try {
            String queryMoneda = "";
            if(moneda != null){
                queryMoneda = " AND f.dolar = :dol";
            }
            Query queryFactura = em.createQuery("SELECT f FROM Factura f WHERE f.cliente.id= :clie AND f.estado<> :est1 AND f.estado<> :est2".concat(queryMoneda));
            queryFactura.setParameter("clie", c.getId());
            queryFactura.setParameter("est1", EstadoPagoFactura.ANULADO.getValor());
            queryFactura.setParameter("est2", EstadoPagoFactura.CANCELADO.getValor());
            if(moneda != null){
                queryFactura.setParameter("dol", moneda.equals(Moneda.DOLAR));
            }
            
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
                } else {
                    if (totalPago < totalFactura) {
                        f.setSaldo(totalFactura - totalPago);
                        f.setSaldoCancelado(totalPago);
                        facturasFinal.add(f);
                    }
                }

            }

//            for (Factura ff : facturasFinal) {
//                System.out.println("factura a devolver-> " + ff.getObservaciones());
//            }
            return facturasFinal;

        } catch (NoResultException nre) {
            return null;
        }
    }
}
