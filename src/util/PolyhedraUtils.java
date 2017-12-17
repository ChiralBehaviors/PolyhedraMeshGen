package util;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3d;

import mesh.Edge;
import mesh.polyhedra.Polyhedron;

/**
 * A utility class for polyhedra.
 * 
 * @author Brian Yao
 */
public class PolyhedraUtils {

	/**
	 * Generates equally spaced vertices along each edge such that each edge
	 * is divided into n equal segments, where n is the "segments" parameter.
	 * The number of vertices added per edge is one less than the "segments"
	 * parameter.
	 * 
	 * For each edge (a,b) where a and b are vertex indices, an entry of the
	 * map will map a to another map, which maps b to an array containing
	 * the indices of the new vertices along the edge (a,b). The array will
	 * be in order, with the first index being the vertex closest to a, and
	 * the last index being the vertex closest to b. The reverse mapping is
	 * also present, with (b,a) being mapped to the same array, but reversed.
	 * The length of each array is one less than the "segments" parameter.
	 * 
	 * @param source   The polyhedron whose edges to use.
	 * @param modify   The polyhedron we are adding the new vertices to.
	 * @param segments The number of segments to divide each edge into.
	 * @return The mapping of each edge to the new vertices along it.
	 */
	public static Map<Integer, Map<Integer, int[]>> divideEdges(Polyhedron source,
			Polyhedron modify, int segments) {
		Map<Integer, Map<Integer, int[]>> newVertices = new HashMap<>();
		int vertexIndex = modify.getVertexPositions().size(); // next index
		for (Edge edge : source.getEdges()) {
			int[] ends = edge.getEnds();
			
			// Generate two new vertices per edge
			Vector3d[] endPositions = edge.getEndLocations();
			Vector3d diff = new Vector3d();
			diff.sub(endPositions[1], endPositions[0]);
			diff.scale(1.0 / segments);
			
			int[] newIndices = new int[segments - 1];
			int[] newIndicesReverse = new int[segments - 1];
			
			// Generate and add vertices for this edge
			for (int i = 1 ; i <= segments - 1 ; i++) {
				Vector3d scaledDiff = new Vector3d();
				scaledDiff.scale(i, diff);
				Vector3d newVertex = new Vector3d();
				newVertex.add(endPositions[0], scaledDiff);
				
				modify.addVertexPosition(newVertex);
				newIndices[i - 1] = vertexIndex;
				newIndicesReverse[segments - i - 1] = vertexIndex;
				vertexIndex++;
			}
			
			// Map the existing edge to the new vertices along it
			if (newVertices.get(ends[0]) == null) {
				newVertices.put(ends[0], new HashMap<Integer, int[]>());
			}
			newVertices.get(ends[0]).put(ends[1], newIndices);
			
			if (newVertices.get(ends[1]) == null) {
				newVertices.put(ends[1], new HashMap<Integer, int[]>());
			}
			newVertices.get(ends[1]).put(ends[0], newIndicesReverse);
		}
		
		return newVertices;
	}
	
}
