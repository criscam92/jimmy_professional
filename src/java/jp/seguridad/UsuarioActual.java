/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.seguridad;

import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import jp.controllers.LoginController;
import jp.entidades.Usuario;

/**
 *
 * @author gurzaf
 */
@ManagedBean(name = "currentUser")
@SessionScoped
@Stateless
public class UsuarioActual {
    
    private Usuario usuario;

    public void set(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Usuario get(){
        if (usuario == null && FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(LoginController.AUTH_KEY) != null) {
            usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().get(LoginController.AUTH_KEY);
        }
        return usuario;
    }
    
    public boolean isAdmin() {
        if (usuario != null) {
            return usuario.isAdmin();
        }
        return false;
    }
    
}
