package com.example.ethhashapp.vm;

import lombok.extern.slf4j.Slf4j;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

/**
 * @author Yann39
 * @since 1.0.0
 */
@VariableResolver(DelegatingVariableResolver.class)
@Slf4j
public class HomeVm {

    /**
     * View-model initialization method.
     */
    @Init
    public void init() {
        log.info("Init " + this.getClass().getSimpleName());
    }

}