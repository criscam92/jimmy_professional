/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import jp.entidades.CambioDevolucion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author arturo
 */
@Stateless
public class CambioDevolucionFacade extends AbstractFacade<CambioDevolucion> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CambioDevolucionFacade() {
        super(CambioDevolucion.class);
    }
    
}
