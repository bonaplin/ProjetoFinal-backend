package aor.project.innovationlab.bean;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

/**
 * Esta classe é responsável por inicializar o sistema.
 Ela cria 1 ou mais elementos de cada se não existerem, para garantir que o sistema tenha dados iniciais.
 */
@Singleton
@Startup
public class StartupBean {

    @Inject
    private SkillTypeBean skillTypeBean;
    @Inject
    private SkillBean skillBean;

    @PostConstruct
    public void init() {
        skillTypeBean.createInitialData();
        skillBean.createInitialData();
    }
}