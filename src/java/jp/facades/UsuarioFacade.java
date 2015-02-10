/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Usuario;
import jp.entidades.Usuario_;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public boolean getUsuarioByNombre(Usuario usuario) {
        try {
            Query query = getEntityManager().createNamedQuery("Usuario.findByUsuario");
            query.setParameter("usuario", usuario.getUsuario());
            query.setMaxResults(1);

            Usuario user = (Usuario) query.getSingleResult();

            if (user != null) {
                if (usuario.getId() != null) {
                    return !user.getId().equals(usuario.getId());
                } else {
                    return true;
                }
            }

        } catch (Exception e) {
            
        }
        return false;
    }

}
