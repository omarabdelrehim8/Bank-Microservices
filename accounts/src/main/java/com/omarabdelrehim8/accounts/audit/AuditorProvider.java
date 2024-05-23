package com.omarabdelrehim8.accounts.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorProvider implements AuditorAware {

    /**
     * Get The Current Auditor Of The Application
     * @return the current auditor
     */
    @Override
    public Optional getCurrentAuditor() {
        return Optional.of("ACCOUNTS_MS");
    }
}
