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
package edu.dfci.cccb.mev.web.domain.social;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;

import edu.dfci.cccb.mev.web.controllers.SubscriptionController;
import edu.dfci.cccb.mev.web.domain.Subscriber;
import lombok.extern.log4j.Log4j;

import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;
import org.springframework.web.context.request.NativeWebRequest;

import edu.dfci.cccb.mev.dataset.rest.google.SecurityContext;
import edu.dfci.cccb.mev.dataset.rest.google.User;

import java.util.Objects;

/**
 * @author levk
 * 
 */
@Log4j
public class SimpleSignInAdapter implements SignInAdapter {

  private final UserCookieGenerator userCookieGenerator = new UserCookieGenerator ();
    private @Inject Provider<Google> gPlus;
    private @Inject Provider<SubscriptionController> ctl;

  public String signIn (String userId, Connection<?> connection, NativeWebRequest request) {
    SecurityContext.setCurrentUser (new User (userId));
    userCookieGenerator.addCookie (userId, request.getNativeResponse (HttpServletResponse.class));
      Person g = gPlus.get().plusOperations().getGoogleProfile();
      try{
        String name = String.format("%s %s", Objects.toString(g.getGivenName(),""), Objects.toString(g.getFamilyName(),""));
        if(name.trim().isEmpty())
          name = Objects.toString(g.getDisplayName(), "");
        ctl.get().subscribe(new Subscriber(g.getAccountEmail(), name));
      }catch(Exception e){
        log.warn("Error while subscribing a google account",e);
      }
    return "/#/datasets/upload?signedin=true";
  }
}
