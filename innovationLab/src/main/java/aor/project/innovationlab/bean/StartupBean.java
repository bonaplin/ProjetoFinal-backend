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
    private SkillBean skillBean;

    @Inject
    private UserBean userBean;

    @Inject
    private LabBean labBean;

    @Inject
    private SupplierBean supplierBean;

    @Inject
    private ProductBean productBean;

    @Inject
    private TaskBean taskBean;

    @Inject
    private InterestBean interestBean;

    @Inject
    private ProjectBean projectBean;

    @PostConstruct
    public void init() {
        labBean.createInitialData();
        supplierBean.createInitialData();
        skillBean.createInitialData();
        userBean.createInitialData();
        productBean.createInitialData();
        projectBean.createInitialData();
        taskBean.createInitialData();
//        interestBean.createInitialData();
    }
}