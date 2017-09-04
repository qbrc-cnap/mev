package edu.dfci.cccb.mev.web.gloud;

import static com.google.cloud.storage.Storage.BucketField.ACL;
import static com.google.cloud.storage.Storage.BucketField.ID;
import static com.google.cloud.storage.Storage.BucketField.NAME;
import static com.google.cloud.storage.Storage.BucketListOption.fields;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import edu.dfci.cccb.mev.configuration.util.contract.Config;
import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.FileTsvInput;
import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Scope;
import org.springframework.social.google.api.Google;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Page;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Acl.Entity.Type;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;

import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetException;
import edu.dfci.cccb.mev.dataset.domain.contract.RawInput;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;

@Log4j
@RestController
public class ProjectController {

  private @Inject Provider<Storage> storage;
  private @Inject ObjectMapper mapper;
  private @Inject Provider<Google> google;
  private @Inject Workspace workspace;
  private @Inject DatasetBuilder dsb;
  @Inject @Named("gcloud-config") Config config;

  @RequestMapping (value = "/cccb-projects", method = GET)
  public List<Project> projects () throws JsonParseException, JsonMappingException, IOException {
    List<Project> projects = new ArrayList<> ();
    Set<String> emails = google.get ().plusOperations ().getGoogleProfile ().getEmailAddresses ();
    Storage s = storage.get();

    for (Iterator<Bucket> i = s.list(Storage.BucketListOption.pageSize(100)).iterateAll(); i.hasNext ();) {
      Bucket b = i.next();
      try {
        for (Acl a : b.getAcl ()) {
          Entity e = a.getEntity();
          if (e.getType() == Type.USER && emails.contains(((User)e).getEmail()))
            projects.add(mapper.readValue(b.get(config.getProperty("gcloud.project.json.filename", "mev.json")).getContent(), Project.class).bucket (b.getName ()));
        }
      } catch (Exception e) {
        log.debug("Skipping bucket " + b.getName());
      }
    }

    return projects;
  }

  @RequestMapping (value = "/cccb-projects", method = POST)
  public void load (final @RequestBody List<Project> projects) throws DatasetException, IOException {
    List<String> eligible = new ArrayList<> ();
    for (Project mine : projects ())
      eligible.add (mine.bucket ());
    for (final Project project : projects)
      if (eligible.contains (project.bucket ()))
        for (final File file : project.files ())
          workspace.put (dsb.build (new RawInput () {
            private final long sz;
            private final InputStream ct;
            private String name = file.name ();

            {
              byte[] bt = storage.get ().get (project.bucket (), file.path ().replaceAll("^/", "")).getContent ();
              sz = bt.length;
              ct = new ByteArrayInputStream (bt);
            }

            public long size () {
              return sz;
            }

            @Override
            public RawInput name (String name) {
              this.name = name;
              return this;
            }

            @Override
            public String name () {
              return name;
            }

            @Override
            public InputStream input () throws IOException {
              return ct;
            }

            @Override
            public String contentType () {
              return TAB_SEPARATED_VALUES;
            }
          }));
  }
}
