package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import jp.entidades.ReciboCaja;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.util.EstadoPagoFactura;

@Stateless
public class ReciboCajaFacade extends AbstractFacade<ReciboCaja> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    @EJB
    private CajaFacade cajaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReciboCajaFacade() {
        super(ReciboCaja.class);
    }
    
    public List<ReciboCaja> getRecibosCaja(){
        List<ReciboCaja> reciboCajas = new ArrayList<>();
        try {
            Query q = em.createQuery("SELECT rc FROM ReciboCaja rc WHERE rc.fecha > :fec");
            q.setParameter("fec", cajaFacade.getCaja().getFechaActualizacion());
            reciboCajas = q.getResultList();
        } catch (Exception e) {
            reciboCajas = null;
        }
        return reciboCajas;
    }
    
}
