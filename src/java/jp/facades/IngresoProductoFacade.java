/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.IngresoProducto;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class IngresoProductoFacade extends AbstractFacade<IngresoProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IngresoProductoFacade() {
        super(IngresoProducto.class);
    }
    
}