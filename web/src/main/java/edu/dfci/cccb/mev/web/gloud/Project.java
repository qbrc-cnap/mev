package edu.dfci.cccb.mev.web.gloud;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties (ignoreUnknown = true)
@Accessors (fluent = true, chain = true)
public class Project {

  private @Getter @JsonProperty ("project_id") String name;
  private @Getter @JsonProperty List<File> files;
  private @Getter @Setter @JsonProperty String bucket;
  private @Getter @Setter @JsonProperty String description;
}
