package kr.junhyung.mainframe.platform.paper.nametag.modelengine;

import com.ticxo.modelengine.api.ModelEngineAPI;
import kr.junhyung.mainframe.platform.paper.condition.ConditionalOnPlugin;
import kr.junhyung.mainframe.platform.paper.nametag.NametagAutoConfiguration;
import kr.junhyung.mainframe.platform.paper.nametag.NametagPassengers;
import org.bukkit.plugin.Plugin;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = NametagAutoConfiguration.class)
@ConditionalOnClass(ModelEngineAPI.class)
public class ModelEngineNametagAutoConfiguration {

    @Bean
    @ConditionalOnBean({NametagPassengers.class, Plugin.class})
    @ConditionalOnMissingBean
    @ConditionalOnPlugin(plugin = "ModelEngine")
    ModelEngineNametagMount modelEngineNametagMount(NametagPassengers passengers, Plugin plugin) {
        return new ModelEngineNametagMount(passengers, plugin);
    }
}
