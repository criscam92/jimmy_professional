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
import jp.entidades.Producto;
import jp.entidades.Promocion;
import jp.entidades.PromocionProducto;
import jp.entidades.Recargo;
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
        System.out.println("Creando visitaProducto de la visita-> " + v.getId());
        boolean complete = false;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Visita visitaTMP = em.find(Visita.class, v.getId());
            visitaTMP.setEstado(EstadoVisita.REALIZADA.getValor());
            visitaTMP.setCalificacionServicio(v.getCalificacionServicio());
            visitaTMP.setPuntualidadServicio(v.getPuntualidadServicio());
            visitaTMP.setCumplioExpectativas(v.getCumplioExpectativas());
            visitaTMP.setObservacionesCliente(v.getObservacionesCliente());
            System.out.println("Setters de la visita.estado-> " + visitaTMP.getEstado() + "\nSetters de la visita.calificacion-> " + visitaTMP.getCalificacionServicio()
                    + "\nSetters de la visita.puntualidad-> " + visitaTMP.getPuntualidadServicio() + "\nSetters de la visita.expectativas-> " + visitaTMP.getCumplioExpectativas());
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

    public boolean createUpdateRecargo(Recargo r) {
        System.out.println("Parametros del recargo nuevo-> \n0- " + r.getId() + " \n1-" + r.getRecargoLocal() + "\n2-" + r.getRecargoNacional()
                + "\n3-" + r.getRecargoInternacional() + "\n4-" + r.getCiudad());
        boolean complete = false;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Recargo recargoTMP = new Recargo();

            recargoTMP.setId(1);
            recargoTMP.setRecargoLocal(r.getRecargoLocal());
            recargoTMP.setRecargoNacional(r.getRecargoNacional());
            recargoTMP.setRecargoInternacional(r.getRecargoInternacional());
            recargoTMP.setCiudad(r.getCiudad());

            em.merge(recargoTMP);
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

    public boolean addPromocionProducto(List<Producto> productos, Promocion promocion) {
        boolean complete = false;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            getEntityManager().persist(promocion);
            Promocion promoTMP = getEntityManager().find(Promocion.class, promocion.getId());

            PromocionProducto promocionProducto;
            for (Producto producto : productos) {
                promocionProducto = new PromocionProducto();
//                promocionProducto.                
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

    public boolean createPromocion(Promocion p, List<PromocionProducto> promocionProductos) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Promocion proTMP = new Promocion();
            proTMP.setDescripcion(p.getDescripcion());
            proTMP.setValor(p.getValor());
            getEntityManager().merge(proTMP);

            for (PromocionProducto pp : promocionProductos) {
                pp.setId(null);
                pp.setPromocion(proTMP);
                getEntityManager().merge(pp);
            }

            userTransaction.commit();
            return true;
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
        return false;
    }

    public boolean updatePromocion(Promocion promocion, List<PromocionProducto> promocionProductosGuardar, List<PromocionProducto> promocionProductosEliminar) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            getEntityManager().merge(promocion);

            for (PromocionProducto pp : promocionProductosEliminar) {
                System.out.println("Eliminar");
                PromocionProducto ppTMP = getEntityManager().find(PromocionProducto.class, pp.getId());
                getEntityManager().remove(ppTMP);
            }

            System.out.println("Sali Eliminar");
            for (PromocionProducto pp : promocionProductosGuardar) {
                pp.setId(null);
                pp.setPromocion(promocion);
                getEntityManager().merge(pp);
            }

            userTransaction.commit();
            return true;
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
        return false;
    }

    public boolean deletePromocion(Promocion promocion) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Query query1 = getEntityManager().createQuery("DELETE FROM PromocionProducto pp WHERE pp.promocion.id = :promo");
            query1.setParameter("promo", promocion.getId());
            query1.executeUpdate();

            Query query2 = getEntityManager().createQuery("DELETE FROM Promocion p WHERE p.id = :promo");
            query2.setParameter("promo", promocion.getId());
            query2.executeUpdate();

            userTransaction.commit();
            return true;
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
        return false;
    }
}