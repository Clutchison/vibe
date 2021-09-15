package com.hutchison.vibe.jda;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandRouter implements ApplicationContextAware {

    ApplicationContext applicationContext;
    static final String basePackage = "com.hutchison.swanbot";
    private Map<Command, VibeRouter> routers;

    @PostConstruct
    private void init() {
        routers = getRouters().stream()
                .collect(Collectors.toMap(
                        r -> r.getClass().getAnnotation(Router.class).value(),
                        r -> r
                ));
    }

    public void route(CommandMessage commandMessage, MessageReceivedEvent event) {
        VibeRouter router = routers.get(commandMessage.getCommand());
        if (router != null) router.route(commandMessage, event);
        else System.out.println("Unhandled command: " + commandMessage.toString());
    }

    private Set<VibeRouter> getRouters() {
        BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);
        TypeFilter tf = new AnnotationTypeFilter(Router.class);
        s.resetFilters(false);
        s.setIncludeAnnotationConfig(false);
        s.addIncludeFilter(tf);
        s.scan(basePackage);
        return Arrays.stream(bdr.getBeanDefinitionNames())
                .map(cn -> (VibeRouter) applicationContext.getBean(cn))
                .collect(Collectors.toSet());
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
