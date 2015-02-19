package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Ciudad;
import jp.entidades.Recargo;

@Stateless
public class RecargoFacade extends AbstractFacade<Recargo> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecargoFacade() {
        super(Recargo.class);
    }

    public boolean isEntityRecargoEmpty() {
        List<Recargo> recargos = new ArrayList<>();
        boolean vacio;
        try {
            Query query = em.createQuery("SELECT r FROM Recargo r");
            recargos = query.getResultList();
            if (!recargos.isEmpty()) {
                vacio = false;
            } else {
                vacio = true;
            }
        } catch (Exception e) {
            vacio = false;
            e.printStackTrace();
        }
        return vacio;
    }

    public Recargo getFirstRecargo() {
        Recargo recargo = new Recargo();
        try {
            Query query = em.createQuery("SELECT r FROM Recargo r");
            query.setMaxResults(1);
            recargo = (Recargo) query.getSingleResult();
            if (recargo != null) {
                return recargo;
            } else {
                recargo = null;
            }
        } catch (Exception e) {
            recargo = null;
            e.printStackTrace();
        }
        return recargo;
    }

    public Float getRecargoByUbicacionCiudad(Ciudad c) {
        System.out.println("Consultando el recargo de= " + c);
        Float cantidadRecargo = 0f;
        Recargo recargo = null;
        String queryGlobal = "SELECT r.ciudad FROM Recargo r WHERE r.ciudad.id= :id";

        Query queryRecargo = em.createQuery("SELECT r FROM Recargo r");
        if (queryRecargo.getResultList().size() > 0) {
            recargo = (Recargo) queryRecargo.getSingleResult();
        }else{
            return cantidadRecargo;
        }
        
        Query queryLocal = em.createQuery(queryGlobal);
        queryLocal.setParameter("id", c.getId());
        System.out.println("Size-> " + queryLocal.getResultList().size());
        if (queryLocal.getResultList().size() > 0) {
            System.out.println("La ciudad es local");
            cantidadRecargo = recargo.getRecargoLocal();
            System.out.println("Cantidad recargo-> " + cantidadRecargo);
        } else {
            String queryPais = "SELECT r FROM Recargo r WHERE r.ciudad.pais.id= :pais";
            Query queryNacional = em.createQuery(queryPais);
            queryNacional.setParameter("pais", c.getPais().getId());
            if (queryNacional.getResultList().size() > 0) {
                recargo = (Recargo) queryNacional.getSingleResult();
                cantidadRecargo = recargo.getRecargoNacional();
                System.out.println("Cantidad recargo-> " + cantidadRecargo);
            }else{
                System.out.println("Ciudad es Internacional");
                cantidadRecargo = recargo.getRecargoInternacional();
                System.out.println("Cantidad recargo-> " + cantidadRecargo);
            }
        }
        return cantidadRecargo;
    }
}
