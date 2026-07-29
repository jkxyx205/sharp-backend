package com.rick.backend;

import com.rick.db.plugin.DbScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

/**
 *  Postgres
 * 将服务器 sql 文件下载到 /Users/rick/Space/Share，然后执行 test
 * @author Rick.Xu
 * @date 2024/11/6 19:17
 */
@Slf4j
public class DBInit2 {

    private static String folder = "/Users/rick/Space/Share"; // challenge-2025-12-09.sql

    private static final String FILE_DATABASE_NAME = "timeline";

    @Test
    public void init() throws IOException, SQLException {
        long start = System.currentTimeMillis();
        File folder = new File(DBInit2.folder);
        File[] files = folder.listFiles(pathname -> pathname.getName().matches(FILE_DATABASE_NAME + "-.*\\.sql"));

        Arrays.sort(files, Comparator.comparing(File::getName));

        File sqlFile = files[files.length - 1];

        String database = FILE_DATABASE_NAME + "-" + sqlFile.getName().substring(sqlFile.getName().length() - 14, sqlFile.getName().length() - 4);
        log.info("database = {}", database);

        String sqlContent = FileUtils.readFileToString(sqlFile, "utf-8");

        // 创建数据库
        DataSource dataSource = dataSource();
        Connection connection = dataSource.getConnection();

        DbScriptUtils.importSQL(connection, "" +
                "DROP DATABASE IF EXISTS \""+database+"\";" +
                "CREATE DATABASE \""+database+"\" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.UTF-8';\n" +
                "ALTER DATABASE \""+database+"\" OWNER TO postgres;");
        connection.close();

        // 导入数据
        connection = dataSource2Database(database).getConnection();
        DbScriptUtils.importSQL(connection, sqlContent);
        connection.close();
        long end = System.currentTimeMillis();
        log.info("processed at costs {} s", (end - start) / 1000);

        // 50 23 * * * /bin/bash -c 'pg_dump -U postgres --inserts -h localhost -d challenge > /usr/local/projects/challenge/datadump/challenge-$(date +\%Y-\%m-\%d).sql && find /usr/local/projects/challenge/datadump -name "dump_*.sql" -mtime +8 -delete' >> /usr/local/projects/challenge/datadump/backup.log 2>&1
    }

    private DataSource dataSource() {
        Properties databaseProperties = getDatabaseProperties();

        // 创建数据源
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(getRootURL(databaseProperties.getProperty("JDBC.URL")));
        dataSource.setUsername(databaseProperties.getProperty("JDBC.USERNAME"));
        dataSource.setPassword(databaseProperties.getProperty("JDBC.PASSWORD"));
        return dataSource;
    }

    private DataSource dataSource2Database(String database) {
        Properties databaseProperties = getDatabaseProperties();

        // 创建数据源
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(getRootURL(databaseProperties.getProperty("JDBC.URL"))  + database);
        dataSource.setUsername(databaseProperties.getProperty("JDBC.USERNAME"));
        dataSource.setPassword(databaseProperties.getProperty("JDBC.PASSWORD"));
        return dataSource;
    }

    private Properties getDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream in = new ClassPathResource(".env-dev.properties").getInputStream()) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props;
    }

    private String getRootURL(String url) {
        return url.replaceAll("(jdbc:postgresql://[^/]+)/.*", "$1") + "/";
    }
}
