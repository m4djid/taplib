package adql.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import adql.parser.ADQLParser;
import adql.query.ADQLQuery;
import tap.metadata.TAPMetadata;
import tap.metadata.TAPTable;
import tap.metadata.TableSetParser;

public class TestSeveralSubQueries {

	@Before
	public void setUp() throws Exception{}

	@Test
	public void test(){
		try{
			TableSetParser tsParser = new TableSetParser();
			TAPMetadata esaMetaData = tsParser.parse(new File("test/adql/db/subquery_test_tables.xml"));
			ArrayList<DBTable> esaTables = new ArrayList<DBTable>(esaMetaData.getNbTables());
			Iterator<TAPTable> itTables = esaMetaData.getTables();
			while(itTables.hasNext())
				esaTables.add(itTables.next());

			ADQLParser adqlParser = new ADQLParser(new DBChecker(esaTables));
			ADQLQuery query = adqlParser.parseQuery("SELECT sel2.*,t1.h_m, t1.j_m, t1.k_m\nFROM (\n  SELECT sel1.*, t3.*\n  FROM (\n  	SELECT *\n    FROM table2 AS t2\n	WHERE 1=CONTAINS(POINT('ICRS', t2.ra, t2.dec), CIRCLE('ICRS', 56.75, 24.1167, 15.))\n  ) AS sel1 JOIN table3 AS t3 ON t3.oid2=sel1.oid2\n) AS sel2 JOIN table1 AS t1 ON sel2.oid=t1.oid");
			assertEquals("SELECT sel2.* , t1.h_m , t1.j_m , t1.k_m\nFROM (SELECT sel1.* , t3.*\nFROM (SELECT *\nFROM table2 AS t2\nWHERE 1 = CONTAINS(POINT('ICRS', t2.ra, t2.dec), CIRCLE('ICRS', 56.75, 24.1167, 15.))) AS sel1 INNER JOIN table3 AS t3 ON ON t3.oid2 = sel1.oid2) AS sel2 INNER JOIN table1 AS t1 ON ON sel2.oid = t1.oid", query.toADQL());

		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("No error expected! (see console for more details)");
		}
	}

}
