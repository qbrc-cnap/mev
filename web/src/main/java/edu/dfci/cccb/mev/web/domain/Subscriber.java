package edu.dfci.cccb.mev.web.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by levk on 3/6/17.
 */
@Entity
@Accessors (fluent = true)
public class Subscriber {
    private @Id @Getter @JsonProperty String email;
    private @Column @JsonProperty (required = false) String name;
}
