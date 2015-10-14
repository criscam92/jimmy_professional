package jp.facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import jp.entidades.ReciboCaja;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.util.CondicionConcepto;

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

    public List<ReciboCaja> getRecibosCaja(boolean isCxcCxp) {
        List<ReciboCaja> reciboCajas;
        String consulta = "SELECT rc FROM ReciboCaja rc WHERE rc.fecha >= :fec AND rc.concepto.cxccxp IN :condicion ORDER BY rc.fecha DESC";
        try {
            Query q = em.createQuery(consulta);
            q.setParameter("fec", cajaFacade.getCaja().getFechaActualizacion());
            if (isCxcCxp) {
                q.setParameter("condicion", CondicionConcepto.getConceptosCxCCxp());
            } else {
                List<Integer> condicionesConceptos = new ArrayList<>();
                condicionesConceptos.add(CondicionConcepto.NINGUNO.getValor());
                q.setParameter("condicion", condicionesConceptos);
            }
            
            reciboCajas = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            reciboCajas = new ArrayList<>();
        }
        return reciboCajas;
    }

}
