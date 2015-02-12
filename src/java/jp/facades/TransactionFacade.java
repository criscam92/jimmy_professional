package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import jp.entidades.Visita;
import jp.entidades.VisitaProducto;
import jp.util.EstadoVisita;

@Stateful
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class TransactionFacade {

    @PersistenceContext(unitName = "jimmy_professionalPU", type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    private UserTransaction userTransaction;

    @Resource
    private SessionContext sessionContext;

    protected EntityManager getEntityManager() {
        return em;
    }

    public List<VisitaProducto> getProductosByVisita(Visita visita) {
        List<VisitaProducto> visitaProductosTMP = new ArrayList<>();
        try {
            Query q = em.createQuery("SELECT vp FROM VisitaProducto vp WHERE vp.visita.id= :visita");
            q.setParameter("visita", visita.getId());
            visitaProductosTMP = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return visitaProductosTMP;
    }

    public boolean createVisitaProducto(List<VisitaProducto> visitaProducto, Visita v) {
        boolean complete = false;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Visita visitaTMP = em.find(Visita.class, v.getId());
            visitaTMP.setEstado(EstadoVisita.REALIZADA.getValor());
            visitaTMP.setCalificacionServicio(v.getCalificacionServicio());
            visitaTMP.setPuntualidadServicio(v.getPuntualidadServicio());
            visitaTMP.setCumplioExpectativas(v.getCumplioExpectativas());
            em.merge(visitaTMP);
            if (!visitaProducto.isEmpty()) {
                for (VisitaProducto visitasProductoTMP : visitaProducto) {
                    visitasProductoTMP.setId(null);
                    visitasProductoTMP.setVisita(visitaTMP);
                    em.persist(visitasProductoTMP);
                }
            }

            userTransaction.commit();
            complete = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            complete = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return complete;
    }

    public boolean anullVisitaProducto(Visita v) {
        boolean result = false;
        Visita visitaTMP;

        List<VisitaProducto> visitaProductosDelete = new ArrayList<>();

        userTransaction = sessionContext.getUserTransaction();

        try {
            userTransaction.begin();
            visitaTMP = em.find(Visita.class, v.getId());
            visitaTMP.setEstado(EstadoVisita.ANULADA.getValor());
            em.merge(visitaTMP);
            userTransaction.commit();
            result = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            result = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }

        return result;
    }
}
