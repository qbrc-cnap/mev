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
package edu.dfci.cccb.mev.kmeans.rest.assembly.tsv;

import static ch.lambdaj.Lambda.join;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.prototype.AbstractTsvHttpMessageConverter;
import edu.dfci.cccb.mev.kmeans.domain.contract.KMeans;

/**
 * @author levk
 * 
 */
public class KMeansTsvMessageConverter extends AbstractTsvHttpMessageConverter<KMeans> {

  /**
   * 
   */
  
  /* (non-Javadoc)
   * @see
   * org.springframework.http.converter.AbstractHttpMessageConverter#supports
   * (java.lang.Class) */
  @Override
  protected boolean supports (Class<?> clazz) {
    return KMeans.class.isAssignableFrom (clazz);
  }

  /* (non-Javadoc)
   * @see
   * org.springframework.http.converter.AbstractHttpMessageConverter#readInternal
   * (java.lang.Class, org.springframework.http.HttpInputMessage) */
  @Override
  protected KMeans readInternal (Class<? extends KMeans> clazz, HttpInputMessage inputMessage) throws IOException,
                                                                                              HttpMessageNotReadableException {
    throw new UnsupportedOperationException ();
  }

  /* (non-Javadoc)
   * @see
   * org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal
   * (java.lang.Object, org.springframework.http.HttpOutputMessage) */
  @Override
  protected void writeInternal (KMeans t, HttpOutputMessage outputMessage) throws IOException,
                                                                          HttpMessageNotWritableException {
    try (PrintStream out = new PrintStream (outputMessage.getBody ())) {
      int index = 0;
      for (Set<String> cluster : t.clusters ())
        out.println ("Cluster " + (++index) + "\t" + join (cluster, ", "));
    }
  }
}
