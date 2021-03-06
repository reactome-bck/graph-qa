package org.reactome.server.qa.tests;

import org.reactome.server.qa.annotations.QATest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@QATest
@Deprecated
public class T063_DuplicatedInferredEntitySets extends QualityAssuranceAbstract {

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public QAPriority getPriority() {
        return null;
    }

    @Override
    public List<String> getHeader() {
        return Arrays.asList("EntitySet1", "EntitySet1_Name", "EntitySet1_Created", "EntitySet2", "EntitySet2_Name", "EntitySet2_Created");
    }

    @Override
    String getQuery() {
        return " MATCH (n1c)<-[:compartment]-(n1:EntitySet)-[:hasMember]->(:PhysicalEntity)<-[:hasMember]-(n2:EntitySet)-[:compartment]->(n2c) " +
                "WHERE ((n1:DefinedSet) OR (n1:OpenSet)) AND ((n2:DefinedSet) OR (n2:OpenSet)) AND " +
                "      ()-[:inferredTo]->(n1) AND NOT ()-[:inferredTo]->(n2) AND NOT n1 = n2 AND n1c = n2c " +
                "WITH DISTINCT n1, n2 " +
                "MATCH (n1)-[r1:hasMember]->(n1pe:PhysicalEntity), " +
                "      (n2)-[r2:hasMember]->(n2pe:PhysicalEntity) " +
                "WITH n1, COLLECT(DISTINCT {pe: n1pe, n: r1.stoichiometry}) AS n1pes, " +
                "     n2, COLLECT(DISTINCT {pe: n2pe, n: r2.stoichiometry}) AS n2pes " +
                "WHERE ALL(c IN n1pes WHERE c IN n2pes) AND ALL(c IN n2pes WHERE c IN n1pes) " +
                "OPTIONAL MATCH (an1)-[:created]->(n1) " +
                "OPTIONAL MATCH (an2)-[:created]->(n2) " +
                "RETURN DISTINCT n1.stId AS EntitySet1, n1.displayName AS EntitySet1_Name, an1.displayName AS EntitySet1_Created, " +
                "                n2.stId AS EntitySet2, n2.displayName AS EntitySet2_Name, an2.displayName AS EntitySet2_Created " +
                "ORDER BY EntitySet1_Created, EntitySet1";
    }
}
