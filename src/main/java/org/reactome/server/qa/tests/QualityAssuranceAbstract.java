package org.reactome.server.qa.tests;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.model.Result;
import org.reactome.server.graph.service.GeneralService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Florian Korninger <florian.korninger@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class QualityAssuranceAbstract implements QualityAssurance {

    private static final String PATH = "./target/qATests/";
    private static final String PREFIX = "QATest";

    abstract String getName();

    abstract String getQuery();

    Boolean doTest() {
        return true;
    }

    @SuppressWarnings({"SameReturnValue", "WeakerAccess"})
    protected Map getMap() {
        return Collections.EMPTY_MAP;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean run(GeneralService genericService) {
        if (doTest()) {
            Result result = genericService.query(getQuery(), getMap());
            if (result == null || !result.iterator().hasNext()) return false;
            try {
                Path path = createFile();
                printResult(result, path);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    void printResult(Result result, Path path) throws IOException {
        print(result, path, "dbId", "stId", "name", "author");
    }

    void print(Result result, Path path, String... attributes) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(StringUtils.join(attributes, ","));
        for (Map<String, Object> map : result) {
            List<String> line = new ArrayList<>();
            for (String attribute : attributes) {
                line.add("\"" + map.get(attribute) + "\"");
            }
            lines.add(StringUtils.join(line, ","));
        }
        Files.write(path, lines, Charset.forName("UTF-8"));
    }

    private Path createFile() throws IOException {
        Path path = Paths.get(PATH + getPrefix() + getName() + ".csv");
        Files.deleteIfExists(path);
        Files.createDirectories(path.getParent());
        Files.createFile(path);
        return path;
    }

    private String getPrefix() {
        return PREFIX + this.getClass().getSimpleName().replaceAll("\\D+", "") + "-";
    }
}
