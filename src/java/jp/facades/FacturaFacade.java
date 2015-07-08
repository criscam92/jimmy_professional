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
import jp.util.TipoPago;

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

    public List<Factura> getFacturasPendientesByCliente(Cliente c) {
        return getFacturasPendientesByCliente(c, null);
    }

    /**
     * Retorna las facturas con pago pendiente del cliente indicado y con la
     * moneda indicada
     *
     * @param c Instancia del cliente de las facturas
     * @param moneda Indica la moneda a buscar, si desea obtener todas las
     * facturas, envíe el valor nulo
     * @return listado de facturas, si no encuentra, retorna nulo
     *
     * @see Moneda
     *
     */
    public List<Factura> getFacturasPendientesByCliente(Cliente c, Moneda moneda) {
        List<Factura> facturasPendientesTMP;
        try {
            String queryMoneda = "";
            if (moneda != null) {
                queryMoneda = " AND f.dolar = :dol";
            }
            Query queryFactura = em.createQuery("SELECT f FROM Factura f WHERE f.cliente.id= :clie AND f.tipoPago = :tipoPago AND f.estado<> :est1 AND f.estado<> :est2".concat(queryMoneda));
            queryFactura.setParameter("clie", c.getId());
            queryFactura.setParameter("est1", EstadoPagoFactura.ANULADO.getValor());
            queryFactura.setParameter("est2", EstadoPagoFactura.CANCELADO.getValor());
            queryFactura.setParameter("tipoPago", TipoPago.CREDITO.getValor());
            if (moneda != null) {
                queryFactura.setParameter("dol", moneda.equals(Moneda.DOLAR));
            }

            facturasPendientesTMP = queryFactura.getResultList();

            facturasPendientesTMP = getFacturasPendientesPago(facturasPendientesTMP);

            return facturasPendientesTMP;

        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Retorna una lista de facturas con pagos pendientes a partir de una lista
     * inicial
     *
     * @param fs listado inicial de facturas a evaluar
     * @return listado final con la lista de facturas que tienen pagos
     * pendientes
     */
    public List<Factura> getFacturasPendientesPago(List<Factura> fs) {
        List<Factura> facturasFinal = new ArrayList<>();

        try {
            for (Factura factura : fs) {
                factura = updatePagoPendiente(factura);
                if (factura != null) {
                    facturasFinal.add(factura);
                }
            }
            return facturasFinal;

        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Obtener una referencia de la factura con los valores de los pagos
     * realizados y los que tenga pendientes
     *
     * @param factura referencia inicial de la factura a evaluar
     * @return la nueva referencia de la factura, en caso de que no tenga pago
     * pendiente retorna nula
     */
    public Factura updatePagoPendiente(Factura factura) {
        try {
            Query q = em.createQuery("SELECT SUM(p.valorTotal) FROM Pago p WHERE p.estado = :estado AND p.factura.id = :f");
            q.setParameter("f", factura.getId());
            q.setParameter("estado", EstadoPagoFactura.REALIZADA.getValor());

            Double totalPago = (Double) q.getSingleResult();
            System.out.println("Pagos a la factura: " + factura + "->" + totalPago);

            Query q2 = em.createQuery("SELECT SUM(f.totalPagar) FROM Factura f WHERE f.id = :f");
            q2.setParameter("f", factura.getId());
            Double totalFactura = (Double) q2.getSingleResult();
            System.out.println("Total Factura: " + factura + "->" + totalFactura);

            if (totalPago == null) {
                System.out.println("Total Pago es nulo");
                factura.setSaldo(factura.getTotalPagar());
                factura.setSaldoCancelado(0d);
                return factura;
            } else {
                System.out.println("Total Pago NO ES NULO");
                if (totalPago < totalFactura) {
                    factura.setSaldo(totalFactura - totalPago);
                    factura.setSaldoCancelado(totalPago);
                    return factura;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Promocion> getPromocionByQuery(String query, Cliente cliente) {
        try {
            List<Promocion> promociones;

            String sql = "SELECT p FROM Promocion p WHERE UPPER(CONCAT(p.codigo,' - ',p.descripcion)) LIKE UPPER(:param)";
            if (cliente.getCategoria() == null) {
                sql += " AND p.categoria IS NULL";
            } else {
                sql += " AND p.categoria.id = :cat";
            }

            Query queryPromocion = getEntityManager().createQuery(sql);

            if (cliente.getCategoria() != null) {
                queryPromocion.setParameter("cat", cliente.getCategoria().getId());
            }
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

    public Factura getFirstFactura() {
        Factura f;
        Query q = em.createQuery("SELECT f FROM Factura f ORDER BY f.id DESC");
        q.setFirstResult(1);
        q.setMaxResults(1);
        f = (Factura) q.getSingleResult();
        return f;
    }

    public boolean anularFactura(Factura factura) {
        try {
            Factura facturaTMP = getEntityManager().find(Factura.class, factura.getId());
            facturaTMP.setEstado(EstadoPagoFactura.ANULADO.getValor());
            getEntityManager().merge(facturaTMP);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
