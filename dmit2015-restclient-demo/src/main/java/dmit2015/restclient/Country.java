package dmit2015.restclient;

import lombok.Data;

@Data
public class Country {

    private String id;
    private String name;
    private Long regionId;
    private String regionName;

}
