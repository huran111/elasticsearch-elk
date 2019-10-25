package demoweb1.demo01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import plugins.MvcInterceptor;

@SpringBootApplication
public class Demo01Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Demo01Application.class, args);
    }
    public static   MvcInterceptor mvcInterceptor=new MvcInterceptor();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mvcInterceptor).addPathPatterns("/demo1/**");
    }
}
