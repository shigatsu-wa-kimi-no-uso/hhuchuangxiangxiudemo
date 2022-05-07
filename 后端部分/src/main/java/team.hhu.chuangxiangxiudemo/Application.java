package team.hhu.chuangxiangxiudemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication
public class Application
{
    public static void main(String[] args)
    {
        System.out.println(SpringVersion.getVersion());
        SpringApplication.run(Application.class, args);
    }
}
