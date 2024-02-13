package com.example.ethhashapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serial;
import java.io.Serializable;

/**
 * Custom application properties.
 *
 * @author Yann39
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "application")
@Getter
@Setter
public class ApplicationConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -7592602136931560686L;

    /**
     * Ethereum RPC server URL.
     */
    private String ethHost;

    /**
     * Account address.
     */
    private String accountAddress;

    /**
     * Smart contract address.
     */
    private String contractAddress;

}
