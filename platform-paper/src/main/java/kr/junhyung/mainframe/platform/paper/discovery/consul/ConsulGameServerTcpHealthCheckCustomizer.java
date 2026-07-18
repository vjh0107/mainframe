package kr.junhyung.mainframe.platform.paper.discovery.consul;

import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.model.http.agent.NewService;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.util.StringUtils;

public class ConsulGameServerTcpHealthCheckCustomizer implements ConsulRegistrationCustomizer {

    private final ConsulDiscoveryProperties properties;

    public ConsulGameServerTcpHealthCheckCustomizer(ConsulDiscoveryProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(ConsulRegistration registration) {
        NewService service = registration.getService();
        NewService.Check check = new NewService.Check();
        check.setTcp(service.getAddress() + ":" + service.getPort());
        check.setInterval(properties.getHealthCheckInterval());
        check.setTimeout(properties.getHealthCheckTimeout());
        if (StringUtils.hasText(properties.getHealthCheckCriticalTimeout())) {
            check.setDeregisterCriticalServiceAfter(properties.getHealthCheckCriticalTimeout());
        }
        service.setCheck(check);
    }
}
