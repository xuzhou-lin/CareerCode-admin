package com.lxz.careercode.admin.config;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 双数据源：admin 库（读写管理数据）+ biz 库（读业务数据、改状态）。
 * 两个库在同一个 MySQL 实例，跨库表在 SQL 里写全限定名即可，不必再拆数据源。
 */
@Configuration
@MapperScans({
        @MapperScan(basePackages = "com.lxz.careercode.admin.mapper.admin",
                sqlSessionTemplateRef = "adminSqlSessionTemplate"),
        @MapperScan(basePackages = "com.lxz.careercode.admin.mapper.biz",
                sqlSessionTemplateRef = "bizSqlSessionTemplate")
})
public class DataSourceConfig {

    /** Druid 密文解密的公钥，和配置里的 password 必须成对，缺一个解不开 */
    private static final String DECRYPT_PUBLIC_KEY =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJybRB7VRRSfJNvNBe4/ebuJoWBoP6aCYcszG3vVRLEeq5Q3ue9/rr7yScYcmJmWXEJ1BnJ2xtETKZpxcSPeW0kCAwEAAQ==";

    @Bean(name = "adminDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.admin")
    public DataSource adminDataSource() {
        return buildDruid();
    }

    @Bean(name = "bizDataSource")
    @ConfigurationProperties("spring.datasource.biz")
    public DataSource bizDataSource() {
        return buildDruid();
    }

    @Bean(name = "adminSqlSessionFactory")
    @Primary
    public SqlSessionFactory adminSqlSessionFactory(@Qualifier("adminDataSource") DataSource dataSource)
            throws Exception {
        return buildFactory(dataSource);
    }

    @Bean(name = "bizSqlSessionFactory")
    public SqlSessionFactory bizSqlSessionFactory(@Qualifier("bizDataSource") DataSource dataSource)
            throws Exception {
        return buildFactory(dataSource);
    }

    @Bean(name = "adminSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate adminSqlSessionTemplate(
            @Qualifier("adminSqlSessionFactory") SqlSessionFactory factory) {
        return new SqlSessionTemplate(factory);
    }

    @Bean(name = "bizSqlSessionTemplate")
    public SqlSessionTemplate bizSqlSessionTemplate(
            @Qualifier("bizSqlSessionFactory") SqlSessionFactory factory) {
        return new SqlSessionTemplate(factory);
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager adminTransactionManager(
            @Qualifier("adminDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "bizTransactionManager")
    public DataSourceTransactionManager bizTransactionManager(
            @Qualifier("bizDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private DruidDataSource buildDruid() {
        // yml 里的 password 是 Druid 密文，绑定进来时顺手解成明文。
        // 直接在 init 前 setConnectionProperties 不够可靠，解密后的值会被再次覆盖。
        return new DruidDataSource() {
            @Override
            public void setPassword(String password) {
                super.setPassword(decrypt(password));
            }
        };
    }

    private static String decrypt(String cipherText) {
        try {
            return ConfigTools.decrypt(DECRYPT_PUBLIC_KEY, cipherText);
        } catch (Exception e) {
            throw new IllegalStateException("数据库密码解密失败，检查 password 与解密公钥是否成对", e);
        }
    }

    private SqlSessionFactory buildFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(configuration);

        // 手动装配的 factory 不会自动带 MP 的分页插件，必须显式加
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        factory.setPlugins(interceptor);

        return factory.getObject();
    }
}
