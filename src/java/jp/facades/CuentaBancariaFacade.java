/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import jp.entidades.CuentaBancaria;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author arturo
 */
@Stateless
public class CuentaBancariaFacade extends AbstractFacade<CuentaBancaria> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CuentaBancariaFacade() {
        super(CuentaBancaria.class);
    }
    
}
