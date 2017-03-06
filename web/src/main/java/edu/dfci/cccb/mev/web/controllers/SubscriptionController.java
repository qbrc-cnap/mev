package edu.dfci.cccb.mev.web.controllers;

import edu.dfci.cccb.mev.web.domain.Subscriber;
import org.springframework.http.HttpStatus;
import org.springframework.social.google.api.Google;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.http.HttpStatus.OK;
import static java.util.Arrays.asList;

/**
 * Created by levk on 3/6/17.
 */
@RestController
public class SubscriptionController {
    private @Inject EntityManager db;
    private @Inject Provider<Google> gPlus;
    private static final Set<String> ALLOWED = new HashSet<>(asList ("lev.v.kuznetsov@gmail.com", "apartensky@gmail.com"));

    @RequestMapping (method = RequestMethod.POST, value = "/subscribe")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void subscribe (@RequestBody Subscriber s) {
        db.persist(s);
    }

    @RequestMapping (method = RequestMethod.DELETE, value = "/unsubscribe")
    @ResponseStatus(HttpStatus.OK)
    public void unsubscribe (@RequestBody Subscriber s) {
        db.remove(db.find(Subscriber.class, s.email()));
    }

    @RequestMapping (method = RequestMethod.GET, value = "/subscribers")
    public Collection<Subscriber> subscribers () {
        if (new HashSet<>(ALLOWED).removeAll(gPlus.get().plusOperations().getGoogleProfile().getEmailAddresses()))
        return db.createQuery("select from " + Subscriber.class.getName()).getResultList();
        else throw new IllegalStateException("YOU BEEN A BAD MONKEY");
    }
}
