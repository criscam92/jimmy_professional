package jp.facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import jp.entidades.Empleado;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.ListaPrecio;
import jp.entidades.Pago;
import jp.entidades.PrecioProducto;
import jp.entidades.Producto;
import jp.entidades.Promocion;
import jp.util.EstadoDespachoFactura;
import jp.util.EstadoFactura;
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
                    cb.equal(fac.get("estado"), EstadoFactura.REALIZADA.getValor())));
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
            queryFactura.setParameter("est1", EstadoFactura.ANULADO.getValor());
            queryFactura.setParameter("est2", EstadoFactura.CANCELADO.getValor());
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
            q.setParameter("estado", EstadoFactura.REALIZADA.getValor());
            Double totalPago = (Double) q.getSingleResult();

            Query q2 = em.createQuery("SELECT SUM(f.totalPagar) FROM Factura f WHERE f.id = :f");
            q2.setParameter("f", factura.getId());
            Double totalFactura = (Double) q2.getSingleResult();
            
            if (totalPago == null) {
                factura.setSaldo(factura.getTotalPagar());
                factura.setSaldoCancelado(0d);
                return factura;
            } else if (totalPago < totalFactura) {
                factura.setSaldo(totalFactura - totalPago);
                factura.setSaldoCancelado(totalPago);
                return factura;
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
                sql += " AND (p.categoria.id = :cat OR p.categoria IS NULL)";
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
            facturaTMP.setEstado(EstadoFactura.ANULADO.getValor());
            facturaTMP.setEstadoDespacho(EstadoDespachoFactura.ANULADO.getValor());
            facturaTMP.setEstadoPago(EstadoPagoFactura.ANULADO.getValor());
            getEntityManager().merge(facturaTMP);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public List<Factura> filterFactura(Empleado empleado, Cliente cliente, int tipoPago, int estado, int estadoDespacho, int estadoPago, Date fechaIni, Date fechaFin) {
        if (empleado != null || cliente != null || tipoPago != -1 || estado != -1 || estadoDespacho != -1 || estadoPago != -1 || (fechaIni != null && fechaFin != null)) {
            String sql = "SELECT f FROM Factura f";
            Map<String, Object> parametros = new HashMap<>();
            if (empleado != null) {
                if (!sql.contains("WHERE")) {
                    sql += " WHERE ";
                }
                sql += "f.empleado.id = :empleado AND ";
                parametros.put("empleado", empleado.getId());
            }
            if (cliente != null) {
                if (!sql.contains("WHERE")) {
                    sql += " WHERE ";
                }
                sql += "f.cliente.id = :cliente AND ";
                parametros.put("cliente", cliente.getId());
            }
            if (tipoPago != -1) {
                if (!sql.contains("WHERE")) {
                    sql += " WHERE ";
                }
                sql += "f.tipoPago = :tipo AND ";
                parametros.put("tipo", tipoPago);
            }
            if (estado != -1) {
                if (!sql.contains("WHERE")) {
                    sql += " WHERE ";
                }
                sql += "f.estado = :estado AND ";
                parametros.put("estado", estado);
            }

            if (estadoDespacho != -1) {
                if (!sql.contains("WHERE")) {
                    sql += " WHERE ";
                }
                sql += "f.estadoDespacho = :estadoDespacho AND ";
                parametros.put("estadoDespacho", estadoDespacho);
            }

            if (estadoPago != -1) {
                if (!sql.contains("WHERE")) {
                    sql += " WHERE ";
                }
                sql += "f.estadoPago = :estadoPago AND ";
                parametros.put("estadoPago", estadoPago);
            }

            boolean fechaIsNull = false;
            if (fechaIni != null && fechaFin != null) {
                fechaIsNull = true;
                if (!sql.contains("WHERE")) {
                    sql += " WHERE ";
                }
                sql += "f.fecha BETWEEN :fechaini AND :fechafin";
                parametros.put("fechaini", fechaIni);
                parametros.put("fechafin", fechaFin);
            }

            if (!fechaIsNull) {
                sql = sql.substring(0, sql.length() - 4);
            }
            
            try {
                Query q = getEntityManager().createQuery(sql);
                for (Map.Entry<String, Object> entrySet : parametros.entrySet()) {
                    String key = entrySet.getKey();
                    Object value = entrySet.getValue();
                    q.setParameter(key, value);
                }
                return q.getResultList();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public List<Factura> getFacturasOrdenadas() {
        try {
            List<Factura> listaFacturas;
            Query query = getEntityManager().createQuery("SELECT f FROM Factura f ORDER BY CAST(f.ordenPedido as INTEGER)");
            listaFacturas = query.getResultList();
            return listaFacturas;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public PrecioProducto getPrecioProductoByListaPrecioAndProducto(ListaPrecio listaPrecio, Producto producto) {
        try {
            Query query = getEntityManager().createQuery("SELECT p FROM PrecioProducto p WHERE p.listaPrecio.id = :lp AND p.producto.id = :pro");
            query.setParameter("lp", listaPrecio.getId());
            query.setParameter("pro", producto.getId());
            return (PrecioProducto) query.getSingleResult();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

}
