package org.kehrbusch.cin7backend;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class ProductionKeyvaultConfig {
    @Value("${db.url}")
    private String dbUrlQualifier;
    @Value("${db.driver}")
    private String dbDriver;
    @Value("${spring.cloud.azure.keyvault.secret.endpoint}")
    private String keyvaultUrl;
    @Value("${azure.keyvault.qualifier.sql.password}")
    private String sqlPasswordQualifier;
    @Value("${keystore.location}")
    private String keystoreLocationQualifier;
    @Value("${azure.keyvault.qualifier.keystore.password}")
    private String keystorePasswordQualifier;

    @Bean
    public SecretClient createSecretClient() {
        return new SecretClientBuilder()
                .vaultUrl(keyvaultUrl)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Bean
    public DataSource dataSource(SecretClient secretClient) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(secretClient.getSecret(dbUrlQualifier).getValue());
        dataSource.setPassword(secretClient.getSecret(sqlPasswordQualifier).getValue());
        return dataSource;
    }

    @Bean
    public JettyServerCustomizer jettyServerCustomizer(SecretClient secretClient) {
        String password = secretClient.getSecret(keystorePasswordQualifier).getValue();
        return server -> {
            // Create an SslContextFactory instance
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(keystoreLocationQualifier); // Path to your keystore
            sslContextFactory.setKeyStorePassword(password); // Keystore password
            sslContextFactory.setKeyManagerPassword(password); // Key password

            // Create an SSL connector with the SslContextFactory
            ServerConnector sslConnector = new ServerConnector(server, sslContextFactory);
            sslConnector.setPort(8080); // Port for HTTPS

            // Remove the default connector (usually HTTP)
            server.setConnectors(new Connector[] { sslConnector });
        };
    }
}
