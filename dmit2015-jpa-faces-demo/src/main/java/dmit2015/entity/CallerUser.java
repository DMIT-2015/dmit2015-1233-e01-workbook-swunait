package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import lombok.Setter;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This Jakarta Persistence class is mapped to a relational database table with the same name.
 * If Java class name does not match database table name, you can use @Table annotation to specify the table name.
 * <p>
 * Each field in this class is mapped to a column with the same name in the mapped database table.
 * If the field name does not match database table column name, you can use the @Column annotation to specify the database table column name.
 * The @Transient annotation can be used on field that is not mapped to a database table column.
 */
@Entity
//@Table(schema = "CustomSchemaName", name="CustomTableName")
@Getter
@Setter
public class CallerUser implements Serializable {

    private static final Logger _logger = Logger.getLogger(CallerUser.class.getName());

    @Id
    @Size(min=3, max=32, message="Enter a username that contains {min} to {max} characters")
    @Column(length=32, unique=true, nullable=false)
    private String username;

    @Column(nullable=false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CallerGroup", joinColumns = {@JoinColumn(name = "username")})
    @Column(name = "groupname", nullable = false)
    private Set<String> groups = new HashSet<>();

    public String getCsvGroups() {
        return groups.stream().collect(Collectors.joining(","));
    }


    public CallerUser() {

    }

    @Version
    private Integer version;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    private void beforePersist() {
        createTime = LocalDateTime.now();
    }

    @PreUpdate
    private void beforeUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        return (
                (obj instanceof CallerUser other) &&
                        Objects.equals(username, other.username)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }


    public static Optional<CallerUser> parseCsv(String line) {
        final var DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] tokens = line.split(DELIMITER, -1);  // The -1 limit allows for any number of fields and not discard trailing empty fields
        /*
         * The order of the columns are: FirstName,LastName,LogonName,PhoneNumber,Department
         * 0 - FirstName
         * 1 - LastName
         * 2 - LogonName
         * 3 - PhoneNumber
         * 4 - Department
         */
        if (tokens.length == 5) {
            CallerUser parsedCallerUser = new CallerUser();

            try {
                // String stringColumnValue = tokens[0].replaceAll("\"","");
                // boolean booleanColumnValue = Boolean.parse(tokens[0]);
                // LocalDate dateColumnValue = tokens[0].isBlank() ? null : LocalDate.parse(tokens[0]);
                // BigDecimal decimalColumnValue = tokens[0].isBlank() ? null : BigDecimal.valueOf(Double.parseDouble(tokens[0]));
                // Integer IntegerColumnValue = tokens[0].isBlank() ? null : Integer.valueOf(tokens[0]);
                // Double DoubleColumnValue = tokens[0].isBlank() ? null : Double.valueOf(tokens[0]);
                // int intColumnValue = tokens[0].isBlank() ? 0 : Integer.parseInt(tokens[0]);
                // double doubleColumnValue = tokens[0].isBlank() ? 0 : Double.parseDouble(tokens[0]);
                String username = tokens[2];
                String role = tokens[4];

                parsedCallerUser.setUsername(username);

                PasswordService passwordService = new DefaultPasswordService();
                String hashedPassword = passwordService.encryptPassword("Password2015");
                parsedCallerUser.setPassword(hashedPassword);

                parsedCallerUser.groups.add(role);

                return Optional.of(parsedCallerUser);
            } catch (Exception ex) {
                _logger.log(Level.FINE, ex.getMessage(), ex);
            }
        }

        return Optional.empty();
    }

}