/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.dfci.cccb.mev.web.configuration;

import edu.dfci.cccb.mev.configuration.util.archaius.ArchaiusConfig;
import edu.dfci.cccb.mev.configuration.util.contract.Config;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j;
import org.apache.commons.dbcp.BasicDataSource;
import org.h2.tools.Server;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

import static java.io.File.separator;
import static java.lang.String.valueOf;
import static java.lang.System.getProperty;
import static org.h2.tools.Server.createWebServer;

/**
 * @author levk
 *
 */
@Configuration
@EnableTransactionManagement
@Log4j
public class PersistenceConfiguration {

  private @Inject Environment environment;

//  @Bean (name = "mevEmf")
//  @Scope("request")
//  public EntityManagerFactory emf () {
//    return Persistence.createEntityManagerFactory("h2");
//  }

//  @Scope("request")
//  @Bean (name="subscriberEm")
//  public EntityManager em (@Named ("mevEmf") EntityManagerFactory emf) {
//    return emf.createEntityManager();
//  }

  @Bean (name="mevEmfb")
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Named("mevdb-config")Config config) {
/*
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);
    vendorAdapter.setShowSql(true);
//      vendorAdapter.setDatabasePlatform(MyAppSettings.getDbPlattform());
    HibernateJpaDialect jpd = new HibernateJpaDialect();
*/

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//    factory.setJpaDialect(jpd);
//    factory.setJpaVendorAdapter(vendorAdapter);
//    factory.setPackagesToScan("edu.dfci.cccb.mev");
//    factory.setPersistenceProvider(new HibernatePersistenceProvider());
//    factory.setDataSource(dataSource());
    factory.setPersistenceUnitName(config.getProperty("MEV_PERSISTENCE_UNIT", "mevdb"));
    return factory;
  }

  @Bean (name="mevTx")
  public PlatformTransactionManager transactionManager (@Named("mevEmfb") EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  @Bean (name="mevdb-config")
  public Config databaseProperties(){
    return new ArchaiusConfig("mevdb.properties");
  }

/*
  @Bean (name = "mev-datasource")
  public DataSource dataSource () {
    BasicDataSource dataSource = new BasicDataSource ();
    dataSource.setDriverClassName (environment.getProperty ("database.driver.class", "org.h2.Driver"));
    dataSource.setUrl (environment.getProperty ("database.url",
            "jdbc:h2:file:"
                    + getProperty ("java.io.tmpdir") + separator
                    + "mev"
                    + ";QUERY_CACHE_SIZE=100000"
                    + ";CACHE_SIZE=1048576"));
    dataSource.setUsername (environment.getProperty ("database.username", "sa"));
    dataSource.setPassword (environment.getProperty ("database.password", ""));
    return dataSource;
  }
*/

  /*@Bean
  public LocalSessionFactoryBean sessionFactory (@Named ("mev-datasource") DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean ();
    sessionFactory.setDataSource (dataSource);
    sessionFactory.setPackagesToScan (environment.getProperty ("session.factory.scan.packages",
            String[].class,
            new String[] { "edu.dfci.cccb.mev", "edu.dfci.cccb.mev.web" }));
    Properties hibernateProperties = new Properties ();
    hibernateProperties.setProperty ("hibernate.hbm2ddl.auto",
            environment.getProperty ("hibernate.hbm2ddl.auto",
                    "create-drop"));
    hibernateProperties.setProperty ("hibernate.dialect",
            environment.getProperty ("hibernate.dialect",
                    "org.hibernate.dialect.H2Dialect"));
    hibernateProperties.setProperty ("hibernate.ejb.naming_strategy",
            environment.getProperty ("hibernate.ejb.naming_strategy",
                    "org.hibernate.cfg.ImprovedNamingStrategy"));
    hibernateProperties.setProperty ("hibernate.connection.charSet",
            environment.getProperty ("hibernate.connection.charSet",
                    "UTF-8"));
    sessionFactory.setHibernateProperties (hibernateProperties);
    return sessionFactory;
  }
*/
/*  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation () {
    return new PersistenceExceptionTranslationPostProcessor ();
  }

  @Bean
  public PlatformTransactionManager transactionManager (@Named ("mev-datasource") DataSource dataSource) {
    return new DataSourceTransactionManager (dataSource);
  }*/


  @Bean @Profile("!test")
  public Lifecycle h2ConsoleServer () {
    return new Lifecycle () {

      private Server server;
      private final int port = environment.getProperty ("h2.console.port", Integer.class, 18043);

      /* (non-Javadoc)
       * @see org.springframework.context.Lifecycle#start() */
      @Override
      @Synchronized
      @PostConstruct
      public void start () {
        try {
          (server = createWebServer ("-webPort", valueOf (port))).start ();
          log.info ("Started H2 console on port " + port);
        } catch (SQLException e) {
          log.warn ("Failed to start H2 console on port " + port, e);
        }
      }

      /* (non-Javadoc)
       * @see org.springframework.context.Lifecycle#stop() */
      @Override
      @Synchronized
      public void stop () {
        server.stop ();
        log.info ("Stopped H2 console");
        server = null;
      }

      /* (non-Javadoc)
       * @see org.springframework.context.Lifecycle#isRunning() */
      @Override
      @Synchronized
      public boolean isRunning () {
        return server != null;
      }
    };
  }

}
