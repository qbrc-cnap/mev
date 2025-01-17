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
package edu.dfci.cccb.mev.dataset.domain.prototype;

import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.COLUMN;
import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.ROW;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import edu.dfci.cccb.mev.dataset.domain.contract.Analyses;
import edu.dfci.cccb.mev.dataset.domain.contract.Annotation;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type;
import edu.dfci.cccb.mev.dataset.domain.contract.InputContentStreamException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.Parser;
import edu.dfci.cccb.mev.dataset.domain.contract.ParserFactory;
import edu.dfci.cccb.mev.dataset.domain.contract.RawInput;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.SelectionBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Selections;
import edu.dfci.cccb.mev.dataset.domain.contract.UnparsableContentTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.ValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Values;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListAnalyses;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListSelections;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDimension;

/**
 * @author levk
 * 
 */
@EqualsAndHashCode
@ToString
@Accessors (fluent = false, chain = true)
@Log4j
public abstract class AbstractDatasetBuilder implements DatasetBuilder {

  private @Getter @Setter @Inject Collection<? extends ParserFactory> parserFactories;
  private @Getter @Setter ValueStoreBuilder valueStoreBuilder;
  private @Getter @Setter @Inject SelectionBuilder selectionBuilder;

  @Inject
  private void configureValueStoreBuilder (Provider<ValueStoreBuilder> valueStoreBuilder) {
    this.valueStoreBuilder = valueStoreBuilder.get ();
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder#build(edu.dfci
   * .cccb.mev.dataset.domain.contract.RawInput) */
  @Override
  public Dataset build (RawInput content) throws DatasetBuilderException,
                                         InvalidDatasetNameException,
                                         InvalidDimensionTypeException {
    if (log.isDebugEnabled ())
      log.debug ("Building dataset..." + content.name ());
    Parser parser;
    for (parser = parser (content); parser.next ();) {
      valueStoreBuilder.add (parser.value (), parser.projection (ROW), parser.projection (COLUMN));
    }
    Values values = valueStoreBuilder.build (parser.rowMap (), parser.columnMap ());
    return aggregate (content.name (), values, analyses (),
                      dimension (ROW, parser.rowKeys (), selections (), annotation ()),
                      dimension (COLUMN, parser.columnKeys (), selections (), annotation ()));
  }

  @Override
  public Dataset build (RawInput content, Selection columnSelection) throws DatasetBuilderException,
                                                                    InvalidDatasetNameException,
                                                                    InvalidDimensionTypeException {
    if (log.isDebugEnabled ())
      log.debug ("**selection: " + columnSelection.keys ());
    Parser parser;
    for (parser = parser (content); parser.next ();) {
      String curColumn = parser.projection (COLUMN);

      if (columnSelection.keys ().contains (curColumn)) {
        valueStoreBuilder.add (parser.value (), parser.projection (ROW), parser.projection (COLUMN));
      } else {
        // do nothing
      }
    }
    Values values = valueStoreBuilder.build ();
    return aggregate (content.name (), values, analyses (),
                      dimension (ROW, parser.rowKeys (), selections (), annotation ()),
                      dimension (COLUMN, parser.columnKeys (), selections (), annotation ()));
  }

  protected Analyses analyses () {
    return new ArrayListAnalyses ();
  }

  protected Dimension dimension (Type type, List<String> keys, Selections selections, Annotation annotation) {
    return new SimpleDimension (type, keys, selections, annotation);
  }

  protected Selections selections () {
    return new ArrayListSelections ();
  }

  protected Annotation annotation () {
    return null; // TODO: add annotation
  }

  protected abstract Dataset aggregate (String name,
                                        Values values,
                                        Analyses analyses,
                                        Dimension... dimensions) throws DatasetBuilderException,
                                                                InvalidDatasetNameException;

  protected Parser parser (RawInput content) throws DatasetBuilderException {
    for (ParserFactory parserFactory : parserFactories)
      if (parserFactory.contentType ().equals (content.contentType ()))
        try {
          return parserFactory.parse (content.input ());
        } catch (IOException e) {
          throw new InputContentStreamException (e);
        }
    throw new UnparsableContentTypeException ().contentType (content.contentType ());
  }
}
