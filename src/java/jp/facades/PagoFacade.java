package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.Pago;
import jp.entidades.PagoDetalle;
import jp.entidades.RelacionFactura;
import jp.util.EstadoPago;

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

}
