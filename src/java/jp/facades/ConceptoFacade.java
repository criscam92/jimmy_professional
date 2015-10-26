package jp.facades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Concepto;
import jp.entidades.Producto;
import jp.util.CondicionConcepto;
import jp.util.TipoConcepto;

@Stateless
public class ConceptoFacade extends AbstractFacade<Concepto> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConceptoFacade() {
        super(Concepto.class);
    }

    @Override
    public List<Concepto> findAll() {
        return super.findAll(true);
    }

    @Override
    public List<Concepto> findAll(boolean asc) {
        return super.findAll(asc);
    }

    public List<Concepto> getConceptosByQuery(String query) {
        try {
            Query q = getEntityManager().createQuery("SELECT c FROM Concepto c WHERE UPPER(CONCAT(c.codigo,' - ',c.detalle)) LIKE UPPER(:param)");
            q.setParameter("param", "%" + query + "%");
            q.setFirstResult(0);
            q.setMaxResults(10);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Concepto> getConceptosCXCCXPByQuery(String query, Boolean isIngreso) {
        try {
            Query q = getEntityManager().createQuery("SELECT c FROM Concepto c WHERE UPPER(CONCAT(c.codigo,' - ',c.detalle)) LIKE UPPER(:param) AND c.cxccxp IN :condiciones");
            q.setParameter("param", "%" + query + "%");
            if (isIngreso == null) {
                q.setParameter("condiciones", CondicionConcepto.getConceptosCxCCxp());
            } else {
                if (isIngreso) {
                    List<Integer> listaCxC = new ArrayList<>();
                    listaCxC.add(CondicionConcepto.CXC.getValor());
                    q.setParameter("condiciones", listaCxC);
                } else {
                    List<Integer> listaCxP = new ArrayList<>();
                    listaCxP.add(CondicionConcepto.CXP.getValor());
                    q.setParameter("condiciones", listaCxP);
                }
            }

            q.setFirstResult(0);
            q.setMaxResults(10);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
