package team.hhu.chuangxiangxiudemo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig
{
    //连接超时时间
    @Value("${spring.rest.connection.timeout}")
    private Integer connectionTimeOut;
    //信息读取超时时间
    @Value("${spring.rest.read.timeout}")
    private Integer readTimeOut;



    /**
     * 声明RestTemplate服务
     * @return RestTemplate
     */
    @Bean
    public RestTemplate registerTemplate(ClientHttpRequestFactory clientHttpRequestFactory)
    {
        RestTemplate restTemplate = new RestTemplate();
        //配置自定义的异常处理
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        return restTemplate;
    }
    /**
     * 初始化请求工厂
     * @return SimpleClientHttpRequestFactory
     */
    @Bean
    public SimpleClientHttpRequestFactory getFactory()
    {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeOut);
        factory.setReadTimeout(readTimeOut);
        return factory;
    }
}
