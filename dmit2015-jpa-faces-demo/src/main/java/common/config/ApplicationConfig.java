package common.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinitions({

//        @DataSourceDefinition(
//                name = "java:app/datasources/h2databaseDS",
//                className = "org.h2.jdbcx.JdbcDataSource",
//                // url="jdbc:h2:file:~/jdk/databases/h2/DMIT2015CourseDB;",
//                url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
//                user = "user2015",
//                password = "Password2015"),

//	@DataSourceDefinition(
//		name="java:app/datasources/LocalMssqlDMIT2015DS",
//		className="com.microsoft.sqlserver.jdbc.SQLServerDataSource",
//		url="jdbc:sqlserver://localhost;databaseName=DMIT2015CourseDB;TrustServerCertificate=true",
//		user="user2015",
//		password="Password2015"),
//
	@DataSourceDefinition(
		name="java:app/datasources/oracleUser2015DS",
		className="oracle.jdbc.xa.client.OracleXADataSource",
		url="jdbc:oracle:thin:@localhost:1521/FREEPDB1",
		user="user2015",
		password="Password2015"),
//	@DataSourceDefinition(
//		name="java:app/datasources/oracleHrDS",
//		className="oracle.jdbc.xa.client.OracleXADataSource",
//		url="jdbc:oracle:thin:@localhost:1521/FREEPDB1",
//		user="HR",
//		password="Password2015"),
//
//	@DataSourceDefinition(
//		name="java:app/datasources/oracleOeDS",
//		className="oracle.jdbc.xa.client.OracleXADataSource",
//		url="jdbc:oracle:thin:@localhost:1521/FREEPDB1",
//		user="OE",
//		password="Password2015"),
//
//	@DataSourceDefinition(
//		name="java:app/datasources/oracleCoDS",
//		className="oracle.jdbc.xa.client.OracleXADataSource",
//		url="jdbc:oracle:thin:@localhost:1521/FREEPDB1",
//		user="CO",
//		password="Password2015"),

//	@DataSourceDefinition(
//		name="java:app/datasources/mysqlDS",
//		className="com.mysql.cj.jdbc.MysqlXADataSource",
//		url="jdbc:mysql://localhost/DMIT2015CourseDB",
//		user="user2015",
//		password="Password2015"),

//	@DataSourceDefinition(
//		name="java:app/datasources/mariadbDS",
//		className="org.mariadb.jdbc.MariaDbDataSource",
//		url="jdbc:mariadb://localhost/DMIT2015CourseDB",
//		user="user2015",
//		password="Password2015"),

//	@DataSourceDefinition(
//		name="java:app/datasources/postgresqlDS",
//		className="org.postgresql.xa.PGXADataSource",
//		url="jdbc:postgresql://localhost/DMIT2015CourseDB",
//		user="user2015",
//		password="Password2015"),

})

@ApplicationScoped
public class ApplicationConfig {

}