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
package edu.dfci.cccb.mev.deseq.domain.simple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import edu.dfci.cccb.mev.deseq.domain.contract.DESeq.Entry;

/**
 * @author levk
 * 
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Accessors (fluent = true)
public class SimpleEntry implements Entry {

  private @Getter final String id;
  private @Getter final Double logFoldChange;
  private @Getter final Double meanExpressionControl;
  private @Getter final Double meanExpressionExperimental;
  private @Getter final Double pValue;
  private @Getter final Double qValue;
}
