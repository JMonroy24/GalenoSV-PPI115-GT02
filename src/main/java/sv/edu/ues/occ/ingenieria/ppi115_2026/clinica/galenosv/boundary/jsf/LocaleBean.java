package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class LocaleBean implements Serializable {

    private Locale locale;

    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        if (locale == null || (!locale.getLanguage().equals("en") && !locale.getLanguage().equals("es"))) {
            locale = new Locale("es");
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void setLanguage(String language) {
        this.locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(this.locale);
    }

    public void changeLanguage(jakarta.faces.event.ValueChangeEvent event) {
        String newLang = event.getNewValue().toString();
        this.locale = new Locale(newLang);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(this.locale);
    }
}
