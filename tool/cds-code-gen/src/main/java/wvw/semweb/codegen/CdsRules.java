package wvw.semweb.codegen;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.FileInputStream;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.sys.JenaSystem;

import wvw.utils.IOUtils;
import wvw.utils.rdf.NS;

public class CdsRules {

	public static void main(String[] args) throws Exception {
		JenaSystem.init();

		N3ModelSpec spec = N3ModelSpec.get(N3_MEM_FP_INF);
		N3Model m = ModelFactory.createN3Model(spec);
		
		m.read(IOUtils.getResourceStream(CdsRules.class,"diabetes-case1.n3"), "N3");
		m.read(IOUtils.getResourceStream(CdsRules.class,"output/case1.ttl"), "N3");
		
		m.listStatements(null, m.createResource(NS.toUri("DMTO:DMTO_0001701")), (Resource) null).forEachRemaining(stmt -> {
			System.out.println(stmt);
		});
	}
}
