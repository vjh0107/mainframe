package kr.junhyung.mainframe.platform.paper.modelengine.disguise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import kr.junhyung.mainframe.platform.paper.condition.ConditionalOnPlugin;
import kr.junhyung.mainframe.platform.paper.disguise.DisguisePassengers;
import kr.junhyung.mainframe.platform.paper.nametag.NametagAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = NametagAutoConfiguration.class)
@ConditionalOnClass(ModelEngineAPI.class)
public class ModelEngineDisguiseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnPlugin(plugin = "ModelEngine")
    ModelEngineDisguiseService modelEngineDisguiseService(ObjectProvider<DisguisePassengers> passengers) {
        return new ModelEngineDisguiseService(passengers.orderedStream().toList());
    }

    @Bean
    @ConditionalOnBean(ModelEngineDisguiseService.class)
    @ConditionalOnMissingBean
    DisguiseVisibilityInterceptor disguiseVisibilityInterceptor(ModelEngineDisguiseService disguises) {
        return new DisguiseVisibilityInterceptor(disguises);
    }
}
