package jp.facades;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Cliente;
import jp.entidades.Factura;
import jp.entidades.Pago;
import jp.entidades.PagoDetalle;
import jp.entidades.PagoPublicidad;
import jp.entidades.RelacionFactura;
import jp.entidades.TipoPagoHelper;
import jp.util.EstadoPago;
import jp.util.TipoPago;
import jp.util.TipoPagoAbono;

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
            query.setParameter("est", EstadoPago.REALIZADO.getValor());
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
            query.setParameter("est", EstadoPago.REALIZADO.getValor());
            pagos = query.getResultList();
        } catch (Exception e) {
            pagos = new ArrayList<>();
        }
        return pagos;
    }

    public boolean existePago(String ordenPago) {
        boolean result = false;
        try {
            Query query = getEntityManager().createQuery("SELECT p FROM Pago p WHERE p.ordenPago = :op");
            query.setParameter("op", ordenPago);
            query.setMaxResults(1);
            Pago pago = (Pago) query.getSingleResult();

            if (pago != null) {
                result = true;
            }

        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public List<PagoDetalle> getListPagoDetalleByPago(Pago pago) {
        List<PagoDetalle> listTMP;
        try {
            Query query1 = getEntityManager().createQuery("SELECT pd FROM PagoDetalle pd WHERE pd.pago.id = :pago");
            query1.setParameter("pago", pago.getId());
            listTMP = query1.getResultList();
        } catch (Exception e) {
            listTMP = null;
        }
        return listTMP;
    }

    public RelacionFactura getRelacionFacturaByPago(Pago selected) {
        RelacionFactura rf;
        try {
            Query query = getEntityManager().createQuery("SELECT r FROM RelacionFactura r WHERE r.id = :id");
            query.setParameter("id", selected.getRelacionFactura().getId());
            rf = (RelacionFactura) query.getSingleResult();
        } catch (Exception e) {
            rf = null;
        }
        return rf;
    }

    @Override
    public List<Pago> findAll() {
        return super.findPagoAll(true);
    }

    @Override
    public List<Pago> findPagoAll(boolean asc) {
        return super.findPagoAll(asc);
    }

    public Pago getPagosByOrdenPago(String ordenPago) {
        try {
            Query query = getEntityManager().createQuery("SELECT p FROM Pago p WHERE p.ordenPago = :op");
            query.setParameter("op", ordenPago);
            query.setMaxResults(1);
            return (Pago) query.getSingleResult();
        } catch (NoResultException e) {
        }
        return null;
    }

    /**
     *
     * @param clienteTMP (Cliente del cual se desea obtener las publicidad o la
     * comision)
     * @param limit (Resultados a devolver)
     * @param tipo (0 - Publicidad, 1 - Comision)
     * @return
     */
    public List<TipoPagoHelper> getPublicidadOrComisionByCliente(Cliente clienteTMP, int limit, int tipo) {
        List<TipoPagoHelper> tipoPagoHelpersTMP = new ArrayList<>();
        try {
            String consulta = "SELECT pd FROM PagoDetalle pd WHERE pd.pago.factura.cliente.id = :cliente AND pd.tipo = :tipoAbono ORDER BY pd.id DESC";
            Query query = getEntityManager().createQuery(consulta);
            query.setParameter("cliente", clienteTMP.getId());

            int tipoAbono;
            if (tipo == 0) {
                tipoAbono = TipoPagoAbono.PUBLICIDAD.getValor();
            } else {
                tipoAbono = TipoPagoAbono.COMISION.getValor();
            }
           
            query.setParameter("tipoAbono", tipoAbono);
            
            if (limit > 0) {    
                query.setMaxResults(limit);
            }         

            List<PagoDetalle> pagoDetalles = query.getResultList();

            if (tipo == 0) {
                for (PagoDetalle pd : pagoDetalles) {
                    if (pd.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                        try {
                            Query query2 = getEntityManager().createQuery("SELECT pp FROM PagoPublicidad pp WHERE pp.pagoDetalle.id = :pagoDetalle");
                            query2.setParameter("pagoDetalle", pd.getId());
                            PagoPublicidad pp = (PagoPublicidad) query2.getSingleResult();

                            boolean repetido = false;
                            for (TipoPagoHelper tph : tipoPagoHelpersTMP) {
                                if (Objects.equals(tph.getTipoPublicidad().getId(), pp.getTipo().getId())) {
                                    pd.setValor(pd.getValor() + tph.getValor());
                                    repetido = true;
                                }
                            }

                            if (!repetido) {
                                TipoPagoHelper tph = new TipoPagoHelper();
                                tph.setId(tipoPagoHelpersTMP.size() + 1);
                                tph.setIdObject(pd.getId());
                                tph.setTipo(pd.getTipo());
                                tph.setTipoPublicidad(pp.getTipo());
                                tph.setValor(pd.getValor());
                                tipoPagoHelpersTMP.add(tph);
                            }

                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                    }
                }
            } else {
                int count = 0;
                for (PagoDetalle pd : pagoDetalles) {
                    if (pd.getTipo() == TipoPagoAbono.COMISION.getValor()) {
                        TipoPagoHelper tph = new TipoPagoHelper();
                        tph.setId(count++);
                        tph.setIdObject(pd.getId());
                        tph.setTipo(pd.getTipo());
                        tph.setValor(pd.getValor());
                        tipoPagoHelpersTMP.add(tph);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            tipoPagoHelpersTMP = new ArrayList<>();
        }
        return tipoPagoHelpersTMP;
    }
    
    public Double getTotalPagosEfectivo(){
        Double total;
        try {
            Query query = em.createQuery("SELECT SUM(p.valorTotal) FROM Pago p WHERE p.estado = :est AND p.formaPago = :tipoPago");
            query.setParameter("est", EstadoPago.REALIZADO.getValor());
            query.setParameter("tipoPago", TipoPago.CONTADO.getValor());
            total = (Double) query.getSingleResult();
        } catch (NoResultException e) {
            total = 0d;
        }
        return total;
    }

}
