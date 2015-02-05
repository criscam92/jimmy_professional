/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import jp.entidades.DevolucionProducto;

/**
 *
 * @author CRISTIAN
 */
@Stateful
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class DevolucionProductoFacade extends AbstractFacade<DevolucionProducto> {
    
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    
    @Resource
    private SessionContext sessionContext;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DevolucionProductoFacade() {
        super(DevolucionProducto.class);
    }

    public boolean createDevolucionProducto(List<DevolucionProducto> devolucionesProducto) {
        boolean complete;
        UserTransaction userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            for (DevolucionProducto devolucionesProductoTMP : devolucionesProducto) {
                em.persist(devolucionesProductoTMP);
            }
            userTransaction.commit();
            complete = true;
        } catch (Exception e) {
            e.printStackTrace();
            complete = false;
        }
        return complete;
    }

}
