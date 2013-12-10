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
package edu.dfci.cccb.mev.web.configuration.resolvers;

import static com.fasterxml.jackson.databind.ser.BeanSerializerFactory.instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import edu.dfci.cccb.mev.web.support.JsonViewResolver;

/**
 * @author levk
 * 
 */
@Configuration
@Log4j
@ToString
public class RestResolverConfiguration {

  @Bean
  public JsonViewResolver jsonViewResolver () {
    return new JsonViewResolver ();
  }

  @Bean
  public ObjectMapper jsonObjectMapper (ApplicationContext context) {
    List<JsonSerializer<?>> serializers = new ArrayList<> ();
    for (JsonSerializer<?> serializer : context.getBeansOfType (JsonSerializer.class).values ())
      serializers.add (serializer);
    log.info ("Registering custom JSON serializers: " + serializers);
    ObjectMapper mapper = new ObjectMapper () {
      private static final long serialVersionUID = 1L;

      /* (non-Javadoc)
       * @see
       * com.fasterxml.jackson.databind.ObjectMapper#writeValue(com.fasterxml
       * .jackson.core.JsonGenerator, java.lang.Object) */
      @Override
      public void writeValue (JsonGenerator jgen, Object value) throws IOException,
                                                               JsonGenerationException,
                                                               JsonMappingException {
        if (log.isDebugEnabled ())
          log.debug ("Writing " + value.getClass ());
        super.writeValue (jgen, value);
      }
    };
    mapper.setSerializerFactory (instance.withAdditionalSerializers (new SimpleSerializers (serializers)));
    return mapper;
  }
}
