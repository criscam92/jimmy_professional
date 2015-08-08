package jp.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import jp.entidades.Usuario;
import jp.facades.UsuarioFacade;
import jp.seguridad.FiltroSeguridad;
import jp.util.JsfUtil;

@ManagedBean(name = "loginController")
@ViewScoped
public class LoginController implements Serializable {

    private Usuario usuario = new Usuario();
    @EJB
    private UsuarioFacade facade;
    private String redirect;
    
    public LoginController() {
    }

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        redirect = context.getExternalContext().getRequestParameterMap().get("redirect");
        System.out.println(redirect);
    }

    public void loggedIn() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(FiltroSeguridad.AUTH_KEY) != null) {
            redireccionar();
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public UsuarioFacade getFacade() {
        return facade;
    }

    public void login() {
        try {
            Usuario user = getFacade().login(usuario.getUsuario(), usuario.getContrasena());
            if (user != null) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FiltroSeguridad.AUTH_KEY, user);
                JsfUtil.addSuccessMessage("Se ingreso correctamente a la aplicación");
            } else {
                JsfUtil.addWarnMessage("No se encontró el usuario y contraseña");
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
        }
        loggedIn();
    }

    public void logoutLink() {

        String url = ((HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest()).getContextPath()
                .concat("/content/?").concat("logout");
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, "Error...");
        }
    }

    private void redireccionar() {
        try {            
            System.out.println("REDIRECT " + redirect);
            if (redirect != null) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(redirect);
            } else {
                FacesContext.getCurrentInstance().getExternalContext().redirect("content/");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ingresando al sistema", "Ocurrio un error al redirigirlo al panel central de la aplicación"));
        }
    }
}
