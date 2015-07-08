package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.ActualizaBaseCaja;

@Stateless
public class ActualizaCajaFacade extends AbstractFacade<ActualizaBaseCaja> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActualizaCajaFacade() {
        super(ActualizaBaseCaja.class);
    }

    public List<ActualizaBaseCaja> getListaActualizacionesCajaByFecha() {
        List<ActualizaBaseCaja> actualizaBaseCajaMenor;
        try {
            Query q = em.createQuery("SELECT ac FROM ActualizaBaseCaja ac ORDER BY ac.fecha DESC");
            actualizaBaseCajaMenor = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            actualizaBaseCajaMenor = null;
        }
        return actualizaBaseCajaMenor;
    }
}
