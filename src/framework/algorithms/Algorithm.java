/* 
 * Copyright (C) 2015 "IMIS-Athena R.C.",
 * Institute for the Management of Information Systems, part of the "Athena" 
 * Research and Innovation Centre in Information, Communication and Knowledge Technologies.
 * [http://www.imis.athena-innovation.gr/]
 *
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
package privacytool.framework.algorithms;

import edu.uci.ics.jung.graph.Graph;
import java.util.List;
import java.util.Map;
import privacytool.framework.algorithms.flash.LatticeNode;
import privacytool.framework.data.Data;
import privacytool.framework.hierarchy.Hierarchy;

/**
 * The interface of the algorithm
 * @author serafeim
 */
public interface Algorithm {
    public void setDataset(Data dataset);
    public void setHierarchies(Map<Integer, Hierarchy> hierarchies);
    public void setArguments(Map<String, Integer> arguments);
    public Object anonymize();

    public Graph getLattice(Map<LatticeNode, Integer> levels, Map<LatticeNode, Integer> positions, 
            List<Integer> numNodesInLevel);
}
