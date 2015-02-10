package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
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
import jp.entidades.Producto;
import jp.entidades.Visita;
import jp.entidades.VisitaProducto;
import jp.util.EstadoVisita;

@Stateful
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class VisitaProductoFacade extends AbstractFacade<VisitaProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU", type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    private UserTransaction userTransaction;    
    
    @Resource
    private SessionContext sessionContext;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VisitaProductoFacade() {
        super(VisitaProducto.class);
    }
    
    public List<Producto> getProductosByVisita(Visita visita){
        System.out.println("==="+visita.getObservacionesCliente()+"==========");
        List<Producto> productosTMP = new ArrayList<>();
        try {
            Query q = em.createQuery("SELECT vp.producto FROM VisitaProducto vp WHERE vp.visita.id= :visita");
            q.setParameter("visita", visita.getId());
            productosTMP = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return productosTMP;
    }
    
    public boolean createVisitaProducto(List<VisitaProducto> visitaProducto, Visita v) {
        boolean complete = false;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Visita visitaTMP = em.find(Visita.class, v.getId());
            visitaTMP.setEstado(EstadoVisita.REALIZADA.getValor());
            for (VisitaProducto visitasProductoTMP : visitaProducto) {
                visitasProductoTMP.setId(null);
                System.out.println("Visita--> " + visitasProductoTMP.getVisita());
//                visitasProductoTMP.getVisita().setEstado(EstadoVisita.REALIZADA.getValor());
                visitasProductoTMP.setVisita(visitaTMP);
                System.out.println("Estado de la visita-> " + visitasProductoTMP.getVisita().getEstado());
                em.persist(visitasProductoTMP);
            }
            userTransaction.commit();
            complete = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {

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
    
}
