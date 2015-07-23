package jp.facades;

import java.util.ArrayList;
import java.util.List;
import jp.entidades.PagoDetalle;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Pago;

@Stateless
public class PagoDetalleFacade extends AbstractFacade<PagoDetalle> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagoDetalleFacade() {
        super(PagoDetalle.class);
    }

    public List<PagoDetalle> getPagoDetallesByPago(Pago pago) {
        List<PagoDetalle> pagoDetalles;
        try {
            Query query = getEntityManager().createQuery("SELECT pd FROM PagoDetalle pd WHERE pd.pago.id = :pago");
            query.setParameter("pago", pago.getId());
            pagoDetalles = query.getResultList();
        } catch (Exception e) {
            pagoDetalles = new ArrayList<>();
        }
        return pagoDetalles;
    }

}
