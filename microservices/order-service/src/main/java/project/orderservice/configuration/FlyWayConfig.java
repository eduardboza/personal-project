// package project.orderservice.configuration;
//
// import lombok.RequiredArgsConstructor;
// import org.flywaydb.core.Flyway;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.env.Environment;
//
// @Configuration
// @RequiredArgsConstructor
// public class FlyWayConfig {
//
//     private final Environment environment;
//
//
//     @Bean(initMethod = "migrate")
//     public Flyway flyway() {
//         return new Flyway(Flyway.configure()
//             .baselineOnMigrate(true)
//             .dataSource(environment.getRequiredProperty("spring.datasource.url"),
//                 environment.getRequiredProperty("spring.datasource.username"),
//                 environment.getRequiredProperty("spring.datasource.password"))
//         );
//     }
//
// }
